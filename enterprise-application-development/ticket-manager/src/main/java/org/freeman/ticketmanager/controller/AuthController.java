package org.freeman.ticketmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.freeman.ticketmanager.common.Result;
import org.freeman.ticketmanager.dto.LoginRequest;
import org.freeman.ticketmanager.dto.RegisterRequest;
import org.freeman.ticketmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证模块", description = "用户注册与登录接口")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户登录", description = "返回 JWT Token，后续请求需在 Header 携带 Authorization: Bearer {token}")
    @PostMapping("/login")
    public Result<Map> login(@RequestBody LoginRequest req) {
        return Result.success(authService.login(req.getUsername(), req.getPassword()));
    }

    @Operation(summary = "用户注册", description = "注册新用户并绑定角色")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return Result.success(null);
    }


}