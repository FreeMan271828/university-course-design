package com.baidu.zhihu.service;

import java.util.List;

import com.baidu.zhihu.model.Post;

public interface PostService {

    // 添加帖子
    public Post add(Post post);

    // 删除帖子
    public boolean delete(Post postInfo);

    // 根据所选信息获取帖子
    public List<Post> get(Post postInfo);

    // 根据id获取帖子
    public Post get(String postId);

    // 获取所有帖子
    public List<Post> listPosts();

    // 根据帖子id添加赞
    public boolean addFavor(String id);
}
