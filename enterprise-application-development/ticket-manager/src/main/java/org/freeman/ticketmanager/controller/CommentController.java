package org.freeman.ticketmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.freeman.ticketmanager.common.Result;
import org.freeman.ticketmanager.dto.CommentDTO; // 假设你有这个DTO
import org.freeman.ticketmanager.entity.Comment;
import org.freeman.ticketmanager.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论/沟通", description = "工单内的对话记录与审计日志")
@RestController
@RequestMapping("/api/v1/tickets/{id}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "获取评论列表", description = "根据当前登录用户角色，自动过滤内部备注(INTERNAL)。客服可见所有，普通用户只看公开。")
    @GetMapping
    public Result<List<Comment>> list(
            @Parameter(description = "工单ID", required = true) @PathVariable Long id) {
        return Result.success(commentService.getComments(id));
    }

    @Operation(summary = "发送评论", description = "支持公开回复或内部备注")
    @PostMapping
    public Result<Void> add(
            @Parameter(description = "工单ID", required = true) @PathVariable Long id,
            @RequestBody CommentDTO dto) {
        commentService.addComment(id, dto.getContent(), dto.getType());
        return Result.success(null);
    }
}