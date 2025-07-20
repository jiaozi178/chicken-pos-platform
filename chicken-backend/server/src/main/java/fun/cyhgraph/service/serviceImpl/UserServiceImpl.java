package fun.cyhgraph.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.cyhgraph.constant.MessageConstant;
import fun.cyhgraph.dto.PageDTO;
import fun.cyhgraph.dto.UserDTO;
import fun.cyhgraph.dto.UserLoginDTO;
import fun.cyhgraph.entity.Employee;
import fun.cyhgraph.entity.User;
import fun.cyhgraph.exception.LoginFailedException;
import fun.cyhgraph.mapper.UserMapper;
import fun.cyhgraph.properties.WeChatProperties;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.service.UserService;
import fun.cyhgraph.utils.HttpClientUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;

@Service
public class UserServiceImpl implements UserService {

    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;


    /**
     * 用户微信登录
     *
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 调用私有方法，其中利用HttpClient来调用微信API服务，获取openid
        String openid = getOpenId(userLoginDTO.getCode());
        // 判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        // 如果是新用户，自动完成注册，插入到数据库
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    public User getUser(Integer id) {
        return userMapper.getById(id);
    }

    /**
     * 修改用户信息
     *
     * @param userDTO
     */
    public void update(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 处理Base64头像图片
        String base64Avatar = userDTO.getPic();
        if (base64Avatar != null && base64Avatar.startsWith("data:image/")) {
            deleteOldAvatar(userDTO.getId());
            String avatarUrl = saveBase64Avatar(base64Avatar);
            user.setPic(avatarUrl); // 存储相对路径URL
        }

        userMapper.update(user);
    }

    /**
     * 保存Base64头像图片到文件系统
     */
    private String saveBase64Avatar(String base64Data) {
        try {
            String[] dataParts = base64Data.split(",");
            String imageData = dataParts[1];
            String mimeType = dataParts[0].split(";")[0].split(":")[1];
            String extension = mimeType.split("/")[1];
            String fileName = java.util.UUID.randomUUID().toString() + "." + extension;

            String uploadDir = "upload" + File.separator + "avatars" + File.separator;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
            String filePath = uploadDir + fileName;
            Files.write(Paths.get(filePath), imageBytes);

            return "/upload/avatars/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("头像图片保存失败");
        }
    }

    /**
     * 删除旧头像文件
     */
    private void deleteOldAvatar(Integer userId) {
        try {
            User oldUser = userMapper.getById(userId);
            if (oldUser != null && oldUser.getPic() != null && oldUser.getPic().startsWith("/upload/avatars/")) {
                String oldFilePath = "upload" + oldUser.getPic().replace("/upload/", "/");
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
        } catch (Exception e) {
            // 删除失败不影响主流程
            System.err.println("删除旧头像失败：" + e.getMessage());
        }
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     * 4参数： appid secret(在小程序平台查看，忘了就重置) 临时登录凭证code 常量authorization_code
     *
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        // 调用微信接口服务，获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        // 利用HttpClient来调用微信的API服务，得到序列化好的json
        String json = HttpClientUtil.doGet(WX_LOGIN, map); // 需自定义HttpClientUtil工具类
        // 解析返回的json对象，并抽取其中的openid
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

    /**
     * 进行用户信息的分页(模糊)查询
     *
     * @param pageDTO
     * @return
     */
    public PageResult userPageList(PageDTO pageDTO) {
        // 传分页参数给PageHelper自动处理，会自动加上limit和count(*)返回分页结果和总记录数
        PageHelper.startPage(pageDTO.getPage(), pageDTO.getPageSize());
        Page<User> pagelist = userMapper.pageQuery(pageDTO);
        return new PageResult(pagelist.getTotal(), pagelist.getResult());
    }

    /**
     * 删除特定用户信息
     * @param id
     */
    public void delete(Integer id) {
        userMapper.delete(id);
    }
}
