package fun.cyhgraph.service.serviceImpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.constant.StatusConstant;
import fun.cyhgraph.dto.SetmealDTO;
import fun.cyhgraph.dto.SetmealPageDTO;
import fun.cyhgraph.entity.Setmeal;
import fun.cyhgraph.entity.SetmealDish;
import fun.cyhgraph.entity.SetmealDishWithPic;
import fun.cyhgraph.exception.DeleteNotAllowedException;
import fun.cyhgraph.mapper.SetmealDishMapper;
import fun.cyhgraph.mapper.SetmealMapper;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.service.SetmealService;
import fun.cyhgraph.vo.DishItemVO;
import fun.cyhgraph.vo.SetmealVO;
import fun.cyhgraph.vo.SetmealWithPicVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        
        // 处理Base64图片
        String base64Pic = setmealDTO.getPic();
        if (base64Pic != null && base64Pic.startsWith("data:image/")) {
            String imageUrl = saveBase64Image(base64Pic);
            setmeal.setPic(imageUrl); // 存储相对路径URL
        }
        
        setmeal.setStatus(1);  // 默认启用套餐
        setmealMapper.addSetmeal(setmeal);
        System.out.println("新增套餐成功！");
        
        // 套餐包含的菜品批量插入
        Integer setmealId = setmeal.getId();
        // 1. 遍历setmealDTO中的菜品列表，每个菜品都为其setmealId字段赋值
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
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
            String uploadDir = "upload" + File.separator + "setmeal" + File.separator;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 解码并保存文件
            byte[] imageBytes = Base64.getDecoder().decode(imageData);
            String filePath = uploadDir + fileName;
            Files.write(Paths.get(filePath), imageBytes);
            
            // 返回相对URL路径
            return "/upload/setmeal/" + fileName;
            
        } catch (Exception e) {
            System.err.println("套餐图片保存失败：" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("套餐图片保存失败");
        }
    }

    /**
     * 套餐条件分页查询
     * @param setmealPageDTO
     * @return
     */
    public PageResult getPageList(SetmealPageDTO setmealPageDTO) {
        PageHelper.startPage(setmealPageDTO.getPage(), setmealPageDTO.getPageSize());
        Page<Setmeal> setmealList = setmealMapper.getPageList(setmealPageDTO);
        return new PageResult(setmealList.getTotal(), setmealList.getResult());
    }

    /**
     * 根据套餐id查询套餐，包括菜品信息
     * @param id
     * @return
     */
    public SetmealVO getSetmealById(Integer id) {
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getDishesBySetmealId(id);
        // 组成SetmealVO后返回
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 起售停售套餐
     * @param id
     */
    public void onOff(Integer id) {
        setmealMapper.onOff(id);
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        
        // 处理图片更新逻辑
        String picData = setmealDTO.getPic();
        if (picData != null && picData.startsWith("data:image/")) {
            // 如果是新上传的Base64图片，需要保存并更新URL
            String imageUrl = saveBase64Image(picData);
            setmeal.setPic(imageUrl);
            
            // 可选：删除旧图片文件（如果需要的话）
            deleteOldImage(setmealDTO.getId());
        }
        // 如果pic是URL格式，直接保持原样更新
        
        // 先修改套餐setmeal
        setmealMapper.update(setmeal);
        // 再修改套餐下的菜品setmealDish
        // 由于行数据可能不同，因此需要先根据setmealId批量删除，再批量插入
        Integer setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 删除旧图片文件
     */
    private void deleteOldImage(Integer setmealId) {
        try {
            // 查询旧的图片路径
            Setmeal oldSetmeal = setmealMapper.getSetmealById(setmealId);
            if (oldSetmeal != null && oldSetmeal.getPic() != null && 
                oldSetmeal.getPic().startsWith("/upload/setmeal/")) {
                
                // 构建文件路径
                String oldFilePath = "upload" + oldSetmeal.getPic().replace("/upload/", "/");
                File oldFile = new File(oldFilePath);
                
                // 删除旧文件
                if (oldFile.exists()) {
                    oldFile.delete();
                    System.out.println("删除旧套餐图片文件：" + oldFilePath);
                }
            }
        } catch (Exception e) {
            // 删除失败不影响主流程
            System.err.println("删除旧套餐图片失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        // 遍历要删除的所有套餐，如果但凡有一个在起售就抛异常
        for(Integer id : ids){
            Setmeal setmeal = setmealMapper.getSetmealById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeleteNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        
        // 删除套餐图片文件
        for(Integer id : ids) {
            deleteOldImage(id);
        }
        
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBatch(ids);
    }

    /**
     * 根据分类id查询所有套餐
     * @return
     */
    public List<Setmeal> getList(Integer categoryId) {
        // 还有一个条件：起售的套餐才能展示在客户端
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> setmealList = setmealMapper.getList(setmeal);
        return setmealList;
    }

    /**
     * 根据套餐id查询所有菜品
     * @param id
     * @return
     */
    public List<DishItemVO> getSetmealDishesById(Integer id) {
        List<DishItemVO> dishItemVOList = setmealMapper.getSetmealDishesById(id);
        return dishItemVOList;
    }

    /**
     * 根据套餐id获取套餐详情，其中菜品都要有pic图片信息
     * @param id
     * @return
     */
    public SetmealWithPicVO getSetmealWithPic(Integer id) {
        Setmeal setmeal = setmealMapper.getSetmealById(id);
        // 该套餐下的每个菜品都需要加上pic字段
        List<SetmealDishWithPic> dishWithPics = setmealDishMapper.getDishesWithPic(id);
        // 组成setmealWithPicVO后返回
        SetmealWithPicVO setmealWithPicVO = new SetmealWithPicVO();
        BeanUtils.copyProperties(setmeal, setmealWithPicVO);
        setmealWithPicVO.setSetmealDishes(dishWithPics);
        return setmealWithPicVO;
    }
}
