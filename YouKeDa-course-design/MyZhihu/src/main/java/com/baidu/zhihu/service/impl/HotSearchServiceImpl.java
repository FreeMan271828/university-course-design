package com.baidu.zhihu.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.baidu.zhihu.model.HotSearch;
import com.baidu.zhihu.service.HotSearchService;

import jakarta.annotation.PostConstruct;

@Service
public class HotSearchServiceImpl implements HotSearchService {

    private static Logger LOG = LoggerFactory.getLogger(HotSearchService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public boolean addHotSearch(HotSearch hotSearch) {
        List<HotSearch> hotSearchs = getHotSearches();
        if (hotSearchs.stream()
                .filter(h -> h.getTitle().equals(hotSearch.getTitle()))
                .findFirst()
                .orElse(null) != null) {
            LOG.info("热搜已存在，添加失败");
        } else {
            mongoTemplate.insert(hotSearch);
            return true;
        }
        return false;
    }

    @Override
    public List<HotSearch> getHotSearches() {
        Query query = new Query();
        List<HotSearch> hotSearchs = mongoTemplate.find(query, HotSearch.class);
        return hotSearchs;
    }

    @PostConstruct
    public void init() {
        addHotSearch(new HotSearch("法国2022年总统选举一轮投票"));
        addHotSearch(new HotSearch("2022五一放假调休共5天"));
        addHotSearch(new HotSearch("四川航空回应飞行员发布仇恨言论"));
        addHotSearch(new HotSearch("11日起广州中小学停课停考"));
        addHotSearch(new HotSearch("中美疾控中心专家发布香港死亡病例数据"));
        addHotSearch(new HotSearch("民航局回应东航坠机传言"));
        addHotSearch(new HotSearch("昆明新增1例系专升本考试学生"));
        addHotSearch(new HotSearch("上海最大家用氧企业停产"));
        addHotSearch(new HotSearch("微信农场"));
        addHotSearch(new HotSearch("面包车撞兰博基尼被起诉"));
    }
}
