package org.freeman.ticketmanager.common;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value(); // 例如 {"ADMIN", "AGENT"}
}