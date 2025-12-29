package com.baidu.zhihu.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.baidu.zhihu.model.Date;
import com.baidu.zhihu.model.Post;
import com.baidu.zhihu.model.User;
import com.baidu.zhihu.service.PostService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.PostConstruct;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    @Override
    public Post add(Post post) {
        // 寻找有没有重复的帖子
        List<Post> list = get(post);
        if (list != null) {
            LOG.info("已有数据，不能重复添加");
            return null;
        }
        mongoTemplate.insert(post);
        LOG.info("添加帖子成功");
        return post;
    }

    @Override
    public boolean addFavor(String id) {
        System.out.println(id);
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        List<Post> posts = mongoTemplate.find(query, Post.class);
        long updateCount = 0;
        for (Post post : posts) {
            Update update = new Update();
            update.set("favorNum", post.getFavorNum() + 1);
            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Post.class);
            updateCount += updateResult.getModifiedCount();
        }

        if (updateCount == 0) {
            LOG.info("修改失败");
            return false;
        }
        LOG.info("修改成功");
        return true;
    }

    @Override
    public boolean delete(Post postInfo) {
        DeleteResult deleteResult = mongoTemplate.remove(postInfo);
        if (deleteResult.getDeletedCount() == 0) {
            LOG.info("没有删除任何记录");
            return false;
        }
        LOG.info("删除了" + deleteResult.getDeletedCount() + "条记录");
        return true;
    }

    @Override
    public List<Post> get(Post postInfo) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("title").is(postInfo.getTitle()),
                Criteria.where("content").is(postInfo.getContent()));
        Query query = new Query(criteria);
        List<Post> retPosts = mongoTemplate.find(query, Post.class);
        if (retPosts.size() == 0) {
            LOG.info("没有获取任何数据");
            return null;
        } else {
            LOG.info("成功获取" + retPosts.size() + "条记录");
            return retPosts;
        }
    }

    @Override
    public Post get(String postId) {
        Criteria criteria = Criteria.where("id").is(postId);
        ;
        Query query = new Query(criteria);
        Post post = mongoTemplate.findOne(query, Post.class);
        if (post == null) {
            LOG.warn("没有找到任何数据");
            return null;
        }
        LOG.info("查询成功");
        return post;
    }

    @Override
    public List<Post> listPosts() {
        List<Post> posts = mongoTemplate.find(new Query(), Post.class);
        return posts;
    }

    // 初始化写入
    @PostConstruct
    private void init() {
        Post post1 = new Post();
        User user1 = new User();
        Date date1 = new Date();
        date1.setYear(2022);
        date1.setMonth(4);
        date1.setDay(1);
        date1.setHour(10);
        date1.setMinute(55);
        post1.setTitle("奥密克戎毒株叒变异了！这次的新变种比德尔塔克戎更厉害？");
        post1.setFavorNum(199);
        user1.setName("极萨学院冷哲");
        post1.setUser(user1);
        post1.setDate(date1);
        post1.setPhoto("/picture/omicron1.png");
        post1.setContent(
                "Omicron毒株的BA.1型和BA.2型的“内部重组”的XE毒株厉害吗？先说结论：需要警惕的是，它可能会表现出更快的传播速度，但因为目前样本量过小，还不能轻易的下这个结论。当局正在检测3个跟Omicron相关的突变重组毒株，分别命名为XD，XF和XE。[1]先来回顾一个概念，什么叫病毒重组。当一个地区有多重毒株传播的时候，就会出现一个人同时感染了不同的毒株，而这些毒株在入侵人体细胞，进行复制的过程中，很可能会发生张冠李戴的现象，这样不同的病毒在同一个细胞里就可能出现交换相同位置的基因组,XD变异毒株隶属于DeltaAY.4分型下，这种Delta毒株获得了Omicron的BA.1的S蛋白突变，也就是我们之前说的德尔塔克戎毒株。根据在公库GISAID上搜索结果显示，目前只有49个样本符合XD的定义，最早报告日期来自于2021年的12月13日，最新报告来自于2022年3月17日。其中主要来自于法国（40例），8个来自于丹麦，一个来自于比利时。另一种XF毒株，也是来自于Delta和Omicron的BA.1的重组，其中主要重组的位置点在非结构性蛋白NSP3的末端附近。这个突变毒株最早在2022年1月7日于英国发现，最后发现于2022年2月14日，在此之后，并没有新的数据报告，同时GISAID数据库中也没有非英国国家的报告数据，所以据此判断XF毒株不太可能传播起来。");
        // post1.addComment(comment);

        Post post2 = new Post();
        User user2 = new User();
        user2.setName("匿名用户");
        post2.setTitle("全球传来坏消息！奥密克戎再次变异，西方彻底放弃，显得很兴奋");
        post2.setFavorNum(0);
        post2.setUser(user2);
        post2.setContent("全球传来坏消息！奥密克戎再次变异，西方彻底放弃，显得很兴奋");
        Date date2 = new Date();
        date2.setYear(2022);
        date2.setMonth(3);
        date2.setDay(11);
        post2.setDate(date2);
        post2.setPhoto("/picture/omicron2.png");
        // post2.addComment(comment);

        Post post3 = new Post();
        User user3 = new User();
        post3.setTitle("现在全球疫情这么严重，新冠病毒奥密克戎变异毒株会怎样演化（变异）？");
        user3.setName("匿名用户");
        post3.setUser(user3);
        post3.setContent("你以为的变异：奥密克戎在美国感染完两亿人，然后玩家点了致死性，两亿病人身上的奥密克戎秒变欧米伽，然后死了一亿八。实际上的变异：奥密克戎XXXXXXXXXXXXXXXX");
        post3.setFavorNum(120);
        Date date3 = new Date();
        date3.setYear(2022);
        date3.setMonth(4);
        date3.setDay(1);
        post3.setDate(date3);
        post3.setPhoto("/picture/omicron3.png");
        // post3.addComment(comment);

        Post post4 = new Post();
        User user4 = new User();
        post4.setTitle("苏州发现奥密克戎新变种，与全球已知毒株均不同源，可能会对疫情产生哪些影响？");
        post4.setUser(user4);
        user4.setName("圣光出鞘");
        post4.setContent("说国内没有变异条件的，与苏州接壤的上海，奥密克戎已经传播几万次了。没事，你们就整活吧，迟早整出来个大活XXXXXX");
        post4.setFavorNum(500);
        Date date4 = new Date();
        date4.setYear(2022);
        date4.setMonth(3);
        date4.setDay(11);
        post4.setDate(date4);
        post4.setPhoto("/picture/omicron4.png");
        // post4.addComment(comment);

        Post post5 = new Post();
        User user5 = new User();
        post5.setTitle("“奥密克戎”变异株引全球警惕澳大利亚人照旧抗议，街头人山人海");
        user5.setName("匿名用户");
        post5.setUser(user5);
        post5.setContent(
                "新冠变异毒株Omicron（奥密克戎）引发全球警惕之际，澳大利亚多地依然在举行抗议。当地时间27日，上千名抗议者聚集在墨尔本市中心示威，反对政府的疫苗接种政策。当天，西澳大利亚州、悉尼和黄金海岸也举行了类似的抗议活动XXXXXX");
        post5.setFavorNum(0);
        Date date5 = new Date();
        date5.setYear(2022);
        date5.setMonth(3);
        date5.setDay(17);
        post5.setDate(date5);
        post5.setPhoto("/picture/omicron5.png");
        // post5.addComment(comment);

        add(post1);
        add(post2);
        add(post3);
        add(post4);
        add(post5);
    }

}
