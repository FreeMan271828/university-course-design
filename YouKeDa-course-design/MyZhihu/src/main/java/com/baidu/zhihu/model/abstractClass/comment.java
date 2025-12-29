package com.baidu.zhihu.model.abstractClass;

import com.baidu.zhihu.model.Date;
import com.baidu.zhihu.model.User;

public abstract class comment {
    // 评论的id
    private String id;
    // 评论的作者
    private User user;
    // 评论的内容
    private String content;
    // 评论的日期
    private Date date;
    // 评论的点赞数
    private int favorNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFavorNum() {
        return favorNum;
    }

    public void setFavorNum(int favorNum) {
        this.favorNum = favorNum;
    }

}