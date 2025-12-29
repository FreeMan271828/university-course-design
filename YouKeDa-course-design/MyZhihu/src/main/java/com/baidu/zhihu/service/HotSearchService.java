package com.baidu.zhihu.service;

import java.util.List;
import com.baidu.zhihu.model.HotSearch;

public interface HotSearchService {

    // 添加热搜
    public boolean addHotSearch(HotSearch hotSearch);

    // 获取热搜
    public List<HotSearch> getHotSearches();

}