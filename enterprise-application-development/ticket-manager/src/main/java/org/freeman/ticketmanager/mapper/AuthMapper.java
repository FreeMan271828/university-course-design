package org.freeman.ticketmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.freeman.ticketmanager.common.UserDetail;
import org.freeman.ticketmanager.entity.User;

import java.util.List;

@Mapper
public interface AuthMapper {
    // 查询：获取用户及其角色列表
    UserDetail selectUserWithName(@Param("username") String username);

    // 注册校验：检查用户名是否存在
    int countByUsername(@Param("username") String username);

    // 注册第一步：插入用户基础信息
    int insertUser(User user);

    // 注册辅助：根据角色编码 (如 "USER") 获取 ID
    Long selectRoleIdByCode(@Param("code") String code);

    // 注册第二步：绑定用户和角色
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    List<User> selectUserList(@Param("roleFilter") String roleFilter,
                              @Param("keyword") String keyword);

    List<User> selectUsersByName(@Param("name") String name);

    User selectByUserId(@Param("userId")  Long userId);
}