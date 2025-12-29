package org.freeman.ticketmanager.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
    private Long id;
    private String username;
    private String password; // 加密后的密码
    private String realName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String email;
}