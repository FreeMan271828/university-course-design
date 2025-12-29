package org.freeman.ticketmanager.service;

import org.freeman.ticketmanager.common.UserContext;
import org.freeman.ticketmanager.entity.Comment;
import org.freeman.ticketmanager.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired private TicketLogService logService; // 注入日志服务

    public void addComment(Long ticketId, String content, String type) {
        // 1. 权限校验
        if ("INTERNAL".equals(type) && !UserContext.isInternalUser()) {
            throw new RuntimeException("权限不足：无法发送内部备注");
        }

        // 2. 插入评论
        Comment comment = new Comment();
        comment.setTicketId(ticketId);
        comment.setUserId(UserContext.getUserId());
        comment.setContent(content);
        comment.setType(type);
        commentMapper.insert(comment);

        // 3. 记录操作日志 (可选，因为评论本身就是记录，但为了审计完整性可以加)
        Map<String, String> details = new HashMap<>();
        details.put("content_preview", content.length() > 20 ? content.substring(0, 20) + "..." : content);
        details.put("type", type);

        logService.record(ticketId, "ADD_COMMENT", details);
    }

    public List<Comment> getComments(Long ticketId) {
        boolean isInternal = UserContext.isInternalUser();
        return commentMapper.selectByTicketId(ticketId, isInternal);
    }
}