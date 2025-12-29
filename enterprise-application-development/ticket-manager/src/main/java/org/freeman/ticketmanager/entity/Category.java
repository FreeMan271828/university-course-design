package org.freeman.ticketmanager.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类编码 (可选, 如 HARDWARE) */
    private String code;

    /** 描述 */
    private String description;

    /** 排序 (越小越靠前) */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}