package fun.cyhgraph.service;

import fun.cyhgraph.dto.PageDTO;
import fun.cyhgraph.dto.UserDTO;
import fun.cyhgraph.dto.UserLoginDTO;
import fun.cyhgraph.entity.User;
import fun.cyhgraph.result.PageResult;

public interface UserService {
    User wxLogin(UserLoginDTO userLoginDTO);

    User getUser(Integer id);

    void update(UserDTO userDTO);

    PageResult userPageList(PageDTO pageDTO);

    public void delete(Integer id) ;
}
