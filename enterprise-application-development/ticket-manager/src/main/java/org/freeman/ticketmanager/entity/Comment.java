package org.freeman.ticketmanager.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable {
    private Long id;
    private Long ticketId;
    private Long userId;
    private String content;

    // 类型: PUBLIC (公开), INTERNAL (内部备注)
    private String type;

    private LocalDateTime createdAt;

    // --- 非数据库字段 (DTO)，用于前端展示 ---
    // 连表查询时填充评论人的名字
    private String creatorName;
    // 连表查询时填充评论人的角色 (可选)
    private String creatorRole;
}