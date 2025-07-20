package fun.cyhgraph.controller.admin;

import fun.cyhgraph.dto.EmployeeDTO;
import fun.cyhgraph.dto.PageDTO;
import fun.cyhgraph.dto.UserDTO;
import fun.cyhgraph.entity.User;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserManageController {

    @Autowired
    private UserService userService;

    /**
     * 客户模糊分页查询
     * @param pageDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> userPageList(PageDTO pageDTO){
        log.info("前端传过来的page参数(关于user)：{}", pageDTO);
        PageResult pageResult = userService.userPageList(pageDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询用户
     * @return
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Integer id){
        log.info("用户id:{}", id);
        User user = userService.getUser(id);
        return Result.success(user);
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        log.info("根据id删除员工,{}", id);
        userService.delete(id);
        return Result.success();
    }
    /**
     * 修改用户信息
     * @param userDTO
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody UserDTO userDTO){
        log.info("新的用户信息：{}", userDTO);
        userService.update(userDTO);
        return Result.success();
    }

//    /**
//     * 新增用户 (不加了,这个功能有点蠢)
//     * @param userDTO
//     * @return
//     */
//    @PostMapping("/add")
//    public Result addUser(@RequestBody UserDTO userDTO){
//        log.info("新增用户的信息：{}", userDTO);
//        userService.addEmployee(userDTO);
//        return Result.success();
//    }

}

