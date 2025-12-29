package org.freeman.ticketmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.freeman.ticketmanager.entity.Category;
import java.util.List;

@Mapper
public interface CategoryMapper {

    // 新增
    int insert(Category category);

    // 修改
    int update(Category category);

    // 删除
    int deleteById(@Param("id") Long id);

    // 根据ID查询
    Category selectById(@Param("id") Long id);

    // 查询列表 (可根据名称模糊搜索，如果name为空则查所有)
    List<Category> selectList(@Param("name") String name);

    // 校验名称是否存在 (用于防止重复添加)
    int countByName(@Param("name") String name, @Param("excludeId") Long excludeId);
}