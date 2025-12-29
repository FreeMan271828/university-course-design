package com.baidu.zhihu.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.baidu.zhihu.model.User;
import com.baidu.zhihu.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    // 完成若用户名不存在，创建用户
    // 完成用户名存在，密码正确，登录
    // 完成用户名存在，密码错误，返回错误

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User addUser(User user) {
        mongoTemplate.insert(user);
        LOG.info("添加用户成功");
        return user;
    }

    @Override
    public List<User> list(String name) {
        Criteria criteria = Criteria.where("name").is(name);
        Query query = new Query(criteria);
        List<User> users = mongoTemplate.find(query, User.class);
        return users;
    }

    @Override
    public User get(String userID) {
        Criteria criteria = Criteria.where("id").is(userID);
        Query query = new Query(criteria);
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            LOG.warn("没有找到指定用户");
        }
        return user;
    }

    @Override
    public User get(String name, String password) {
        Criteria criteria = Criteria.where("name").is(name);
        criteria.andOperator(Criteria.where("password").is(password));
        Query query = new Query(criteria);
        User user = mongoTemplate.findOne(query, User.class);
        if (user == null) {
            LOG.warn("没有找到用户");
            return null;
        }
        return user;
    }
}
