package org.freeman.ticketmanager.service;

import org.freeman.ticketmanager.common.UserContext;
import org.freeman.ticketmanager.entity.SlaPolicy;
import org.freeman.ticketmanager.entity.Ticket;
import org.freeman.ticketmanager.mapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketService {

    @Autowired private TicketMapper ticketMapper;
    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private TicketLogService logService;

    public Ticket getTicketDetail(Long id) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        return ticket;
    }

    public List<Ticket> getTicketList(Long creatorId, Long assigneeId, String status, Long categoryId) {
        // 这里可以加入 PageHelper.startPage(page, size) 做分页
        return ticketMapper.selectList(creatorId, assigneeId, status, categoryId);
    }

    @Transactional
    public void updateTicketStatus(Long id, String newStatus) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) throw new RuntimeException("工单不存在");

        String oldStatus = ticket.getStatus();

        ticket.setStatus(newStatus);
        if ("CLOSED".equals(oldStatus)) {
            throw new RuntimeException("已关闭的工单无法修改状态");
        }
        if("PENDING".equals(oldStatus)) {
            ticketMapper.updateStatus(ticket, LocalDateTime.now(), null);
        }
        else if("OPEN".equals(oldStatus)) {
            ticketMapper.updateStatus(ticket, null, LocalDateTime.now());
        }
        // 记录审计日志
        Map<String, Object> logDetails = Map.of(
                "old_status", oldStatus,
                "new_status", newStatus
        );
        logService.record(id, "STATUS_CHANGE", logDetails);
    }

    @Transactional
    public void updateTicketAssignee(Long id, Long assigneeId) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) throw new RuntimeException("工单不存在");

        String oldStatus = ticket.getStatus();
        if ("CLOSED".equals(oldStatus)) {
            throw new RuntimeException("已关闭的工单无法修改状态");
        }
        ticketMapper.updateAssignee(ticket, assigneeId);
        // 记录审计日志
        Map<String, Object> logDetails = Map.of(
                "old_status", oldStatus,
                "assignee", assigneeId
        );
        logService.record(id, "STATUS_CHANGE", logDetails);
    }

    /**
     * 创建工单
     */
    @Transactional
    public Long createTicket(Ticket ticket) {
        // 1. 获取当前登录用户
        ticket.setCreatorId(UserContext.getUserId());

        // 2. 生成流水号 (Redis INCR)
        ticket.setSerialNo(generateSerialNo());
        ticket.setStatus("PENDING");

        SlaPolicy sla = getSlaPolicy(ticket.getPriority());
        LocalDateTime now = LocalDateTime.now();
        if (sla != null) {
            ticket.setResponseDeadline(now.plusHours(sla.getResponseHours()));
            ticket.setResolveDeadline(now.plusHours(sla.getResolveHours()));
        }

        // 4. 落库 (PG)
        ticketMapper.insert(ticket);
        logService.record(ticket.getId(), "CREATE", ticket);
        return ticket.getId();
    }

    /**
     * 指派工单
     */
    @Transactional
    public void assignTicket(Long ticketId, Long assigneeId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) throw new RuntimeException("Ticket not found");

        // 状态机简单校验
        if ("CLOSED".equals(ticket.getStatus())) {
            throw new RuntimeException("Cannot assign closed ticket");
        }

        ticket.setStatus("OPEN"); // 自动转为处理中
        ticket.setAssigneeId(assigneeId);
        Map<String, Object> details = new HashMap<>();
        details.put("assigneeId", assigneeId);
        logService.record(ticketId, "ASSIGN", details);
        ticketMapper.updateStatus(ticket, null, null);
    }

    /**
     * 获取SLA配置 (Spring Cache)
     * value = cacheName, key = Redis Key suffix
     */
    @Cacheable(value = "config:sla", key = "#priority", unless = "#result == null")
    public SlaPolicy getSlaPolicy(String priority) {
        return ticketMapper.selectSlaPolicy(priority);
    }



    private String generateSerialNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long seq = redisTemplate.opsForValue().increment("ticket:seq:" + dateStr);
        return String.format("T-%s-%03d", dateStr, seq);
    }
}