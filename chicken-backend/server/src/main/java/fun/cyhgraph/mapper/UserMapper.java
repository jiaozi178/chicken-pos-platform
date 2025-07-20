package fun.cyhgraph.mapper;

import com.github.pagehelper.Page;
import fun.cyhgraph.dto.PageDTO;
import fun.cyhgraph.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("select * from user where id = #{id}")
    User getById(Integer id);

    void update(User user);

    Integer countByMap(Map map);

    Page<User> pageQuery(PageDTO pageDTO);

    @Delete("delete from user where id = #{id}")
    void delete(Integer id);
}
