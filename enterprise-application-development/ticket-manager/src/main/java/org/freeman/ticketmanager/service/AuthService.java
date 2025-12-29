package org.freeman.ticketmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.freeman.ticketmanager.common.UserDetail;
import org.freeman.ticketmanager.dto.RegisterRequest;
import org.freeman.ticketmanager.entity.User;
import org.freeman.ticketmanager.mapper.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    @Autowired private AuthMapper authMapper;
    @Autowired private StringRedisTemplate redisTemplate;
    private ObjectMapper objectMapper = new  ObjectMapper();

    public Map login(String username, String password) {
        UserDetail user = authMapper.selectUserWithName(username);
        if (user == null) throw new RuntimeException("用户不存在");
        String token = UUID.randomUUID().toString().replace("-", "");
        try {
            String userJson = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set("auth:token:" + token, userJson, 2, TimeUnit.HOURS);
        } catch (Exception e) { throw new RuntimeException(e); }
        Map result = new HashMap();
        result.put("token", token);
        result.put("name",user.getUsername());
        result.put("role", user.getRoles());
        result.put("id", user.getUserId());
        return result;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (authMapper.countByUsername(req.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 1. 插入用户
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword("{noop}" + req.getPassword()); // 模拟加密
        user.setRealName(req.getRealName());
        user.setEmail(req.getEmail());
        authMapper.insertUser(user);

        // 2. 查找角色ID并关联
        Long roleId = authMapper.selectRoleIdByCode(req.getRoleCode());
        if (roleId == null) throw new RuntimeException("非法角色");

        authMapper.insertUserRole(user.getId(), roleId);
    }
}