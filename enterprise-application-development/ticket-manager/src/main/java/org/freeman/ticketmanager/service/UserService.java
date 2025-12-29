package org.freeman.ticketmanager.service;

import org.freeman.ticketmanager.common.UserContext;
import org.freeman.ticketmanager.common.UserDetail;
import org.freeman.ticketmanager.entity.User;
import org.freeman.ticketmanager.mapper.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private AuthMapper authMapper;

    // 修改方法签名，增加 keyword 参数
    public List<User> getUserList(String keyword) {
        UserDetail currentUser = UserContext.get();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        List<String> roles = currentUser.getRoles();
        String roleFilter = null;

        // 权限判断逻辑保持不变
        if (roles.contains("ADMIN") || roles.contains("AGENT")) {
            roleFilter = null;
        } else {
            roleFilter = "USER";
        }

        // 3. 执行查询，传入 keyword
        return authMapper.selectUserList(roleFilter, keyword);
    }

    public User getUserById(Long id) {
        return authMapper.selectByUserId(id);
    }

    public List<Long> getUserIdsByName(String username) {
        List<User>users = authMapper.selectUsersByName(username);
        return users.stream().map(User::getId).toList();
    }
}
