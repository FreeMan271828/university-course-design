package org.freeman.ticketmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.freeman.ticketmanager.common.RequireRole;
import org.freeman.ticketmanager.entity.Category;
import org.freeman.ticketmanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理控制器
 */
@Tag(name = "工单分类管理", description = "提供工单分类的增删改查功能")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "获取分类列表", description = "支持根据名称或编码模糊搜索")
    @GetMapping
    public ResponseEntity<List<Category>> getList(
            @Parameter(description = "分类名称(可选)") @RequestParam(required = false) String keyword) {
        List<Category> list = categoryService.getList(keyword);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @RequireRole("ADMIN")
    public ResponseEntity<String> create(@RequestBody Category category) {
        try {
            categoryService.create(category);
            return ResponseEntity.ok("创建成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "更新分类")
    @PutMapping
    @RequireRole("ADMIN")
    public ResponseEntity<String> update(@RequestBody Category category) {
        try {
            categoryService.update(category);
            return ResponseEntity.ok("更新成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "删除分类", description = "注意：如果分类已被工单使用，可能会删除失败")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Parameter(description = "分类ID", required = true) @PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            // 捕获外键约束异常等
            return ResponseEntity.badRequest().body("删除失败，该分类可能已被使用");
        }
    }
}