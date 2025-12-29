package org.freeman.ticketmanager.entity;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Ticket implements Serializable {
    private Long id;
    private String serialNo;
    private String description;
    private String title;
    private String priority;
    private String status;
    private String category;
    private Long categoryId;

    // PG JSONB
    private Map<String, Object> customFields;

    // SLA
    private LocalDateTime responseDeadline;
    private LocalDateTime resolveDeadline;

    private LocalDateTime responseAt;
    private LocalDateTime resolveAt;

    private Long creatorId;
    private Long assigneeId;
    private String creatorName;
    private String assigneeName;
    private LocalDateTime createdAt;
}

