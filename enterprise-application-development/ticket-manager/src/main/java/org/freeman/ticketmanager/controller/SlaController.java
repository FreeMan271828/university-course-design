package org.freeman.ticketmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.freeman.ticketmanager.common.RequireRole;
import org.freeman.ticketmanager.common.Result;
import org.freeman.ticketmanager.entity.SlaPolicy;
import org.freeman.ticketmanager.service.SlaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SLA配置", description = "服务等级协议配置 (仅管理员)")
@RestController
@RequestMapping("/api/v1/sla")
public class SlaController {

    @Autowired
    private SlaService slaService;

    @Operation(summary = "获取所有SLA策略")
    @GetMapping
    public Result<List<SlaPolicy>> list() {
        return Result.success(slaService.findAll());
    }

    @Operation(summary = "保存/更新SLA策略", description = "修改后会自动刷新 Redis 缓存")
    @PostMapping
    @RequireRole("ADMIN")
    public Result<Void> save(@RequestBody SlaPolicy sla) {
        slaService.saveOrUpdate(sla);
        return Result.success(null);
    }

    @Operation(summary = "删除SLA策略")
    @DeleteMapping("/{priority}")
    @RequireRole("ADMIN")
    public Result<Void> delete(
            @Parameter(description = "优先级 (如 HIGH, LOW)", required = true) @PathVariable String priority) {
        slaService.delete(priority);
        return Result.success(null);
    }
}