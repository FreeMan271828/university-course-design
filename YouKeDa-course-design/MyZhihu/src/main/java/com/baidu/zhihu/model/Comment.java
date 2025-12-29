package com.baidu.zhihu.model;

import java.util.ArrayList;
import java.util.List;

import com.baidu.zhihu.model.abstractClass.comment;

//针对帖子的回复评论
public class Comment extends comment {

    List<Reply> replies = new ArrayList<>();

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public void addReply(Reply reply) {
        replies.add(reply);
    }
}
