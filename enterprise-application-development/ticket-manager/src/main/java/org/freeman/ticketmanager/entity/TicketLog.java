package org.freeman.ticketmanager.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class TicketLog implements Serializable {
    private Long id;
    private Long ticketId;
    private Long operatorId; // 操作人ID

    private String action;
    private Map<String, Object> details;
    private LocalDateTime createdAt;

    // --- 非数据库字段 ---
    private String operatorName; // 操作人名字
}