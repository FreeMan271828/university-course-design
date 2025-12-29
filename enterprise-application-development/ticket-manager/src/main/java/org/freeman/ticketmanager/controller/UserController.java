package org.freeman.ticketmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.freeman.ticketmanager.common.Result;
import org.freeman.ticketmanager.entity.User;
import org.freeman.ticketmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "用户管理")
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户列表", description = "支持按名称模糊搜索。权限控制：管理员可见所有，普通用户仅可见普通用户")
    @GetMapping
    public Result<List<User>> list(
            @Parameter(description = "搜索关键字(用户名或真实姓名)")
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(userService.getUserList(keyword));
    }

    @Operation(summary = "根据Id获取用户")
    @GetMapping("/id")
    public Result<User> getById(@Parameter(description = "目标Id")long id) {
        return Result.success(userService.getUserById(id));
    }
}
