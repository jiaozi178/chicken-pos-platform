package fun.cyhgraph.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.constant.StatusConstant;
import fun.cyhgraph.dto.DishDTO;
import fun.cyhgraph.dto.DishPageDTO;
import fun.cyhgraph.entity.Dish;
import fun.cyhgraph.entity.DishFlavor;
import fun.cyhgraph.entity.Setmeal;
import fun.cyhgraph.exception.DeleteNotAllowedException;
import fun.cyhgraph.mapper.DishFlavorMapper;
import fun.cyhgraph.mapper.DishMapper;
import fun.cyhgraph.mapper.SetmealDishMapper;
import fun.cyhgraph.mapper.SetmealMapper;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.service.DishService;
import fun.cyhgraph.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    public void addDishWithFlavor(DishDTO dishDTO) {
        // 不仅要向dish表添加数据，还要向dish_flavor表添加口味数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        
        // 处理Base64图片
        String base64Pic = dishDTO.getPic();
        if (base64Pic != null && base64Pic.startsWith("data:image/")) {
            String imageUrl = saveBase64Image(base64Pic);
            dish.setPic(imageUrl); // 存储相对路径URL
        }
        
        dish.setStatus(1);
        dishMapper.addDish(dish);
        System.out.println("新增dish成功！");
        
        Integer dishId = dish.getId();
        
        // 处理口味数据
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        if (flavorList != null && !flavorList.isEmpty()) {
            flavorList.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavorList);
        }
    }

    /**
     * 保存Base64图片到文件系统
     */
    private String saveBase64Image(String base64Data) {
        try {
            // 解析Base64数据：data:image/jpeg;base64,/9j/4AAQ...
            String[] dataParts = base64Data.split(",");
            String imageData = dataParts[1]; // 实际的Base64数据
            String mimeType = dataParts[0].split(";")[0].split(":")[1]; // image/jpeg
            
            // 根据MIME类型确定文件扩展名
            String extension = mimeType.split("/")[1]; // jpeg
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "." + extension;
            
            // 修改为相对路径，确保在 chicken-backend 目录下
            String uploadDir = "upload" + File.separator + "chicken_menu" + File.separator;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 解码并保存文件
            byte[] imageBytes = Base64.getDecoder().decode(imageData);
            String filePath = uploadDir + fileName;
            Files.write(Paths.get(filePath), imageBytes);
            
            // 返回相对URL路径
            return "/upload/chicken_menu/" + fileName;
            
        } catch (Exception e) {
            throw new RuntimeException("图片保存失败");
        }
    }

    /**
     * 根据条件page信息分页查询菜品
     * @param dishPageDTO
     * @return
     */
    public PageResult getPageList(DishPageDTO dishPageDTO) {
        PageHelper.startPage(dishPageDTO.getPage(), dishPageDTO.getPageSize());
        Page<Dish> dishList = dishMapper.getPageList(dishPageDTO);
        return new PageResult(dishList.getTotal(), dishList.getResult());
    }

    /**
     * 根据id查询对应的dish和对应的flavors
     * @param id
     * @return
     */
    public DishVO getDishWithFlavorById(Integer id) {
        // 使用 useGenerateKey 和 keyProperty 来返回对应的id
        Dish dish = dishMapper.getById(id);
        // 根据id查询对应的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 更新菜品和对应口味数据
     * @param dishDTO
     */
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        
        // 处理图片更新逻辑
        String picData = dishDTO.getPic();
        if (picData != null && picData.startsWith("data:image/")) {
            // 如果是新上传的Base64图片，需要保存并更新URL
            String imageUrl = saveBase64Image(picData);
            dish.setPic(imageUrl);
            
            // 可选：删除旧图片文件（如果需要的话）
            deleteOldImage(dishDTO.getId());
        }
        // 如果pic是URL格式，直接保持原样更新
        
        // 先根据id更新菜品数据
        dishMapper.update(dish);
        
        // 再根据where dishId=菜品id，去批量更新对应的口味数据
        Integer dishId = dishDTO.getId();
        // 原来的口味和新的口味的行数据量可能不一样，不能直接更新，只能批量删除再批量插入
        // 1. 根据dishId批量删除
        dishFlavorMapper.deleteByDishId(dishId);
        List<DishFlavor> flavorList = dishDTO.getFlavors();
        if (flavorList != null && !flavorList.isEmpty()){
            flavorList.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            // 2. 再批量插入口味数据
            dishFlavorMapper.insertBatch(flavorList);
        }
    }

    /**
     * 删除旧图片文件（可选功能）
     */
    private void deleteOldImage(Integer dishId) {
        try {
            // 查询旧的图片路径
            Dish oldDish = dishMapper.getById(dishId);
            if (oldDish != null && oldDish.getPic() != null && 
                oldDish.getPic().startsWith("/upload/chicken_menu/")) {
                
                // 构建文件路径
                String oldFilePath = "upload" + oldDish.getPic().replace("/upload/", "/");
                File oldFile = new File(oldFilePath);
                
                // 删除旧文件
                if (oldFile.exists()) {
                    oldFile.delete();
                    System.out.println("删除旧图片文件：" + oldFilePath);
                }
            }
        } catch (Exception e) {
            // 删除失败不影响主流程
            System.err.println("删除旧图片失败：" + e.getMessage());
        }
    }

    /**
     * 根据菜品id列表，批量删除菜品
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        // 1. 遍历所有菜品，如果有任何一个在起售，则抛异常表示不能删除
        for (Integer id : ids){
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeleteNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 2. 遍历所有菜品，如果有关联套餐也不能删除
        List<Integer> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()){
            throw new DeleteNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 可以批量删除菜品和对应口味数据了
        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteBatch(ids);
    }

    /**
     * 根据id修改起售停售状态
     * @param id
     */
    public void onOff(Integer id) {
        dishMapper.onOff(id);
    }

    /**
     * 获取对应分类下的所有菜品，包括对应口味
     * @param dish
     * @return
     */
    public List<DishVO> getDishesWithFlavorById(Dish dish) {
        List<Dish> dishList = dishMapper.getList(dish);
        List<DishVO> dishVOList = new ArrayList<>();
        // 对菜品列表的每个菜品都加上对应口味，分别封装成DishVO再返回
        for (Dish d : dishList){
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

}
