package org.freeman.ticketmanager.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private String roleCode; // 注册时指定角色: USER, AGENT...
    private String email;
}
