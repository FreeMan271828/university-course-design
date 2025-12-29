package org.freeman.ticketmanager.controller;

import lombok.extern.java.Log;
import org.freeman.ticketmanager.common.RequireRole;
import org.freeman.ticketmanager.common.Result;
import org.freeman.ticketmanager.entity.TicketLog;
import org.freeman.ticketmanager.service.TicketLogService;
import org.freeman.ticketmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ticket-logs")
public class TicketLogController {

    @Autowired private TicketLogService ticketLogService;

    /**
     * 查询日志列表
     * GET /api/ticket-logs?ticketId=1001&operatorId=5
     */
    @GetMapping
    @RequireRole("ADMIN")
    public Result getLogs(TicketLog query) {
        return Result.success(ticketLogService.searchLogs(query));
    }

    /**
     * 手动记录日志 (通常日志是内部业务触发，但也可能需要外部记录)
     */
    @PostMapping
    @RequireRole("ADMIN")
    public Result<String> createLog(@RequestBody TicketLog ticketLog) {
        ticketLogService.record(ticketLog.getTicketId(), ticketLog.getAction(), ticketLog.getDetails());
        return Result.success("Log created successfully with ID: " + ticketLog.getId());
    }
}