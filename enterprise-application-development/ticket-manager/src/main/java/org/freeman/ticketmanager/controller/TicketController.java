package org.freeman.ticketmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.freeman.ticketmanager.common.RequireRole;
import org.freeman.ticketmanager.common.Result;
import org.freeman.ticketmanager.entity.Ticket;
import org.freeman.ticketmanager.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "工单管理", description = "工单的创建、流转与查询")
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Operation(summary = "获取工单详情")
    @GetMapping("/{id}")
    public Result<Ticket> getDetail(@PathVariable Long id) {
        return Result.success(ticketService.getTicketDetail(id));
    }

    @Operation(summary = "查询工单列表", description = "支持查询全部、查询我创建的、查询指派给我的")
    @GetMapping
    public Result<List<Ticket>> list(
            @Parameter(description = "创建人ID (查询某人创建的工单)") @RequestParam(required = false) Long creatorId,
            @Parameter(description = "处理人ID (查询某人被指派的工单)") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "状态过滤") @RequestParam(required = false) String status,
            @Parameter(description = "分类过滤") @RequestParam(required = false) Long categoryId
    ) {

        return Result.success(ticketService.getTicketList(creatorId, assigneeId, status, categoryId));
    }

    @Operation(summary = "更新工单状态", description = "例如将状态改为 RESOLVED 或 CLOSED")
    @PatchMapping("/{id}/status")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        if (status == null) {
            return Result.success("Status can not be null");
        }
        ticketService.updateTicketStatus(id, status);
        return Result.success(null);
    }

    @Operation(summary = "更新工单状态", description = "例如将状态改为 RESOLVED 或 CLOSED")
    @PatchMapping("/{id}/assignee")
    public Result<String> updateAssignee(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String assigneeId = body.get("assigneeId");
        if (assigneeId == null) {
            return Result.success("assigneeId can not be null");
        }
        ticketService.updateTicketStatus(id, assigneeId);
        return Result.success(null);
    }

    @Operation(summary = "创建工单", description = "提交工单基础信息，系统会自动计算 SLA 和生成流水号")
    @PostMapping
    public Result<Long> create(@RequestBody Ticket ticket) {
        return Result.success(ticketService.createTicket(ticket));
    }

    @Operation(summary = "指派工单", description = "仅限客服或管理员操作。指派后状态自动变更为 OPEN")
    @PatchMapping("/{id}/assign")
    public Result<Void> assign(
            @Parameter(description = "工单ID", required = true) @PathVariable Long id,
            @Parameter(description = "被指派的客服ID", required = true) @RequestParam Long assigneeId) {
        ticketService.assignTicket(id, assigneeId);
        return Result.success(null);
    }
}