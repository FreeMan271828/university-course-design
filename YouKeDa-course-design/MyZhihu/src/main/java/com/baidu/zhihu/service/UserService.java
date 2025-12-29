package com.baidu.zhihu.service;

import java.util.List;

import com.baidu.zhihu.model.User;

public interface UserService {

    // 完成若用户名不存在，创建用户
    // 完成用户名存在，密码正确，登录
    // 完成用户名存在，密码错误，返回错误

    public User addUser(User user);

    public List<User> list(String name);

    public User get(String userID);

    public User get(String name, String password);
}
