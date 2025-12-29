package org.freeman.ticketmanager.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 添加这行注解
public class UserDetail {
    private Long userId;
    private String username;
    private List<String> roles;

    public boolean isInternal() {
        return roles != null && (roles.contains("ADMIN") || roles.contains("AGENT"));
    }
}
