package com.baidu.zhihu.service;

import java.util.List;
import com.baidu.zhihu.model.Comment;
import com.baidu.zhihu.model.Post;
import com.baidu.zhihu.model.Reply;

public interface CommentSerice {
    // 有两种回复，一种是直接回复帖子的
    // 一种是回复评论的

    // 根据帖子添加评论
    public boolean addComment(Post post, Comment comment);

    // 根据评论添加回复
    public boolean addReply(Post post, Comment comment, Reply reply);

    // 根据评论删除特定评论
    public boolean dropComment(Post post, Comment comment);

    // 根据回复删除回复
    public boolean dropReply(Post post, Comment comment, Reply reply);

    // 根据帖子获取所有的评论
    public List<Comment> getComments(Post post);

    // 根据评论获取所有的回复
    public List<Reply> getReplies(Post post, Comment comment);
}