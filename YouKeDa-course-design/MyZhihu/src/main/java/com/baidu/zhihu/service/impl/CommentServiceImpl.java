package com.baidu.zhihu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baidu.zhihu.model.Comment;
import com.baidu.zhihu.model.Date;
import com.baidu.zhihu.model.Post;
import com.baidu.zhihu.model.Reply;
import com.baidu.zhihu.model.User;
import com.baidu.zhihu.service.CommentSerice;
import com.baidu.zhihu.service.PostService;
import com.mongodb.client.result.UpdateResult;

import jakarta.annotation.PostConstruct;

@Service
public class CommentServiceImpl implements CommentSerice {

    private static Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PostService postService;

    @Override
    public boolean addComment(Post post, Comment comment) {
        LOG.info("开始为" + post.getTitle() + "添加评论" + comment.getContent());
        Criteria criteria = getPostCriteria(post);
        Query query = new Query(criteria);
        Post tarPost = mongoTemplate.findOne(query, Post.class);
        if (tarPost == null) {
            LOG.warn("没有找到指定的评论");
            return false;
        }
        Update update = new Update();
        List<Comment> newComments = tarPost.getComments();
        if (!newComments.isEmpty()) {
            for (Comment tempComment : newComments) {
                if (tempComment.getId().equals(comment.getId())
                        || (tempComment.getContent().equals(comment.getContent()))) {
                    LOG.warn("要添加的评论已经存在");
                    return false;
                }
            }
        }
        newComments.add(comment);
        update.set("comments", newComments);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Post.class);
        if (updateResult.getModifiedCount() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean addReply(Post post, Comment comment, Reply reply) {
        Criteria criteria = getPostCriteria(post);
        Query query = new Query(criteria);
        Post tarPost = mongoTemplate.findOne(query, Post.class);
        if (tarPost == null) {
            LOG.info("未找到指定帖子");
            return false;
        }
        List<Comment> comments = tarPost.getComments();
        if (comments.isEmpty()) {
            LOG.error("指定的帖子下没有评论");
            return false;
        }
        for (Comment tarComment : comments) {
            if (tarComment.getId().equals(comment.getId()) || tarComment.getContent().equals(comment.getContent())) {
                List<Reply> replies = tarComment.getReplies();
                if (!replies.isEmpty()) {
                    for (Reply tempReply : replies) {
                        if (tempReply.getId().equals(reply.getId())
                                || tempReply.getContent().equals(reply.getContent())) {
                            LOG.warn("要添加的回复已存在");
                            return false;
                        }
                    }
                }
                tarComment.addReply(reply);
                Update update = new Update();
                update.set("comments", comments);
                mongoTemplate.updateFirst(query, update, Post.class);
                return true;
            }
        }
        LOG.warn("没有找到指定的评论");
        return false;
    }

    @Override
    public boolean dropComment(Post post, Comment comment) {
        Criteria criteria = getPostCriteria(post);
        Query query = new Query(criteria);
        Post tarPost = mongoTemplate.findOne(query, Post.class);
        if (tarPost == null) {
            LOG.info("未找到指定帖子");
            return false;
        }
        long dropCount = 0;
        List<Comment> comments = tarPost.getComments();
        Comment dropComment = comments.stream()
                .filter(c -> c.getId().equals(comment.getId()) ||
                        c.getContent().equals(comment.getContent()))
                .findFirst()
                .orElse(null);
        if (dropComment == null) {
            LOG.info("未找到" + tarPost.getTitle() + "下的评论");
        }
        comments.remove(dropComment);
        Update update = new Update();
        update.set("comments", comments);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Post.class);
        dropCount += updateResult.getModifiedCount();

        LOG.info("删除成功，删除" + dropCount + "条记录");
        return true;
    }

    @Override
    public boolean dropReply(Post post, Comment comment, Reply reply) {
        Criteria criteria = getPostCriteria(post);
        Query query = new Query(criteria);
        Post tarPost = mongoTemplate.findOne(query, Post.class);
        if (tarPost == null) {
            LOG.info("未找到指定帖子");
        }

        long dropCount = 0;
        List<Comment> comments = tarPost.getComments();
        Comment tarComment = comments.stream()
                .filter(c -> c.getId().equals(comment.getId()) ||
                        c.getContent().equals(comment.getContent()))
                .findFirst()
                .orElse(null);
        if (tarComment == null) {
            LOG.info("未找到" + tarPost.getTitle() + "下的评论");
        }
        List<Reply> replies = tarComment.getReplies();
        Reply tarReply = replies.stream()
                .filter(r -> r.getId().equals(reply.getId()) ||
                        r.getContent().equals(reply.getContent()))
                .findFirst()
                .orElse(null);
        if (tarReply == null) {
            LOG.info("未找到对应的回复");
        }
        replies.remove(tarReply);
        Update update = new Update();
        update.set("replies", replies);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Post.class);
        dropCount += updateResult.getModifiedCount();

        LOG.info("删除成功，删除" + dropCount + "条记录");
        return true;
    }

    @Override
    public List<Comment> getComments(Post post) {
        Criteria criteria = getPostCriteria(post);
        Query query = new Query(criteria);
        Post tarPost = mongoTemplate.findOne(query, Post.class);
        if (tarPost == null) {
            LOG.info("未找到指定帖子");
            return null;
        }
        List<Comment> comments = tarPost.getComments();
        return comments;
    }

    @Override
    public List<Reply> getReplies(Post post, Comment comment) {
        Criteria criteria = getPostCriteria(post);
        Query query = new Query(criteria);
        Post tarPost = mongoTemplate.findOne(query, Post.class);
        if (tarPost == null) {
            LOG.info("未找到指定帖子");
            return null;
        }
        List<Comment> comments = tarPost.getComments();
        if (comments.size() == 0) {
            LOG.info("该帖子下没有评论");
            return null;
        }
        Comment tarComment = comments.stream()
                .filter(c -> c.getId().equals(comment.getId()) ||
                        c.getContent().equals(comment.getContent()))
                .findFirst()
                .orElse(null);
        if (tarComment == null) {
            LOG.info("该帖子下没有对应的评论");
            return null;
        }
        List<Reply> replies = tarComment.getReplies();
        return replies;
    }

    public Criteria getPostCriteria(Post post) {
        List<Criteria> criterias = new ArrayList<>();
        if (StringUtils.hasText(post.getTitle()))
            criterias.add(Criteria.where("title").is(post.getTitle()));
        if (StringUtils.hasText(post.getContent()))
            criterias.add(Criteria.where("content").is(post.getContent()));
        if (StringUtils.hasText(post.getId()))
            criterias.add(Criteria.where("id").is(post.getId()));
        Criteria criteria = new Criteria();
        criteria.orOperator(criterias.toArray(new Criteria[] {}));
        return criteria;
    }

    @PostConstruct
    public void init() {
        List<Post> posts = postService.listPosts();
        // 构建第一个帖子评论
        Comment comment1 = new Comment();
        User user1 = new User();
        user1.setName("天真岁月不可欺");
        comment1.setUser(user1);
        comment1.setFavorNum(14);
        comment1.setContent("多谢科普");
        Date date1 = new Date();
        date1.setYear(2022);
        date1.setMonth(3);
        date1.setDay(30);
        comment1.setDate(date1);

        Reply reply1 = new Reply();
        User user2 = new User();
        user2.setName("知乎用户");
        reply1.setUser(user2);
        reply1.setFavorNum(45);
        Date date3 = new Date();
        date3.setYear(2022);
        date3.setMonth(3);
        date3.setDay(21);
        reply1.setDate(date3);
        reply1.setContent(
                "嗨，这话说的。于我而言，把我之前不了解的科学知识，用通俗易懂的方式讲出来，就是科普。可能宁是专家，不需要这些。仁者见仁吧。另外，观点不同可以理解，但没必要直接贴标签吧？论迹不论心，而且，我不觉得答主有蹭热点的必要。");
        reply1.setId(UUID.randomUUID().toString());

        Reply reply2 = new Reply();
        User user3 = new User();
        user3.setName("木木");
        reply2.setContent("那碍着您什么事了？管的未免太宽了");
        reply2.setFavorNum(0);
        reply2.setUser(user3);
        reply2.setId(UUID.randomUUID().toString());
        Date date4 = new Date();
        date4.setYear(2022);
        date4.setMonth(3);
        date4.setDay(22);
        reply2.setDate(date4);

        Comment comment2 = new Comment();
        User user4 = new User();
        user4.setName("猜猜看吧");
        comment2.setUser(user4);
        comment2.setContent("以色列发现第四针没太大用，准备打第五针，你们自己想");
        Date date2 = new Date();
        date2.setYear(2022);
        date2.setMonth(3);
        date2.setDay(30);
        comment2.setDate(date2);
        posts.forEach(post -> {
            comment2.setId(UUID.randomUUID().toString());
            comment1.setId(UUID.randomUUID().toString());
            addComment(post, comment1);
            addComment(post, comment2);
            reply1.setId(UUID.randomUUID().toString());
            reply2.setId(UUID.randomUUID().toString());

            addReply(post, comment1, reply1);
            addReply(post, comment2, reply2);
        });
    }
}
