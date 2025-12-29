package org.freeman.ticketmanager.service;

import org.freeman.ticketmanager.entity.Category;
import org.freeman.ticketmanager.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getList(String keyword) {
        return categoryMapper.selectList(keyword);
    }

    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean create(Category category) {
        if(!categoryMapper.selectList(category.getName()).isEmpty()) {
            throw new RuntimeException("分类名称已存在");
        }
        return categoryMapper.insert(category) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean update(Category category) {
        if(category.getName() !=null && !categoryMapper.selectList(category.getName()).isEmpty()) {
            throw new RuntimeException("分类名称已存在");
        }
        return categoryMapper.update(category) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }
}