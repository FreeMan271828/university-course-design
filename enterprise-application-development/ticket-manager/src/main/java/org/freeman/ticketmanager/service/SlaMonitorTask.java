package org.freeman.ticketmanager.service;

import org.freeman.ticketmanager.entity.Ticket;
import org.freeman.ticketmanager.entity.User;
import org.freeman.ticketmanager.mapper.AuthMapper; // 或 UserMapper
import org.freeman.ticketmanager.mapper.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SlaMonitorTask {

    @Autowired private TicketMapper ticketMapper;
    @Autowired private AuthMapper authMapper;
    @Autowired private MailService emailService;
    @Autowired private StringRedisTemplate redisTemplate;

    @Value("${ticket.sla.response-notice-minutes:60}")
    private int responseNoticeMinutes;

    @Value("${ticket.sla.resolve-notice-minutes:30}")
    private int resolveNoticeMinutes;

    private final Logger log = LoggerFactory.getLogger(SlaMonitorTask.class);


    @Scheduled(cron = "0 * * * * ?")
    public void checkSlaDeadlines() {
        log.info("开始检查截止时间");
        LocalDateTime now = LocalDateTime.now();
        // 计算查询的时间范围
        LocalDateTime responseThreshold = now.plusMinutes(responseNoticeMinutes);
        LocalDateTime resolveThreshold = now.plusMinutes(resolveNoticeMinutes);

        // 1. 先查出所有即将超时的工单
        List<Ticket> tickets = ticketMapper.selectApproachingTickets(responseThreshold, resolveThreshold);

        for (Ticket t : tickets) {
            // 2. 获取 assigneeId
            Long assigneeId = t.getAssigneeId();
            if (assigneeId == null) continue;

            // 3. 单独查询用户信息获取邮箱 (解耦逻辑)
            User user = authMapper.selectByUserId(assigneeId);

            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                // 4. 处理预警逻辑
                processTicket(t, user, now);
            }
        }
    }

    private void processTicket(Ticket t, User user, LocalDateTime now) {
        String email = user.getEmail();
        String name = user.getRealName();

        // --- 检查响应超时 ---
        if (t.getResponseAt() == null && t.getResponseDeadline() != null) {
            long minutesLeft = ChronoUnit.MINUTES.between(now, t.getResponseDeadline());
            // 只有当时间小于配置的预警时间，且不是那种已经过期很久的旧数据(-60)
            if (minutesLeft <= responseNoticeMinutes && minutesLeft > -60) {
                sendAlert(t, email, name, "RESPONSE", minutesLeft);
            }
        }

        // --- 检查解决超时 ---
        if (t.getResolveDeadline() != null) {
            long minutesLeft = ChronoUnit.MINUTES.between(now, t.getResolveDeadline());
            if (minutesLeft <= resolveNoticeMinutes && minutesLeft > -60) {
                sendAlert(t, email, name, "RESOLVE", minutesLeft);
            }
        }
    }

    private void sendAlert(Ticket t, String email, String name, String type, long minutesLeft) {
        // Redis 防重 (12小时内不重复发)
        String redisKey = "sla:notice:" + type + ":" + t.getId();
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(redisKey, "1");

        if (Boolean.TRUE.equals(isNew)) {
            String action = "RESPONSE".equals(type) ? "响应" : "解决";
            String urgentPrefix = minutesLeft < 0 ? "【已超时】" : "【即将超时】";
            LocalDateTime deadline = "RESPONSE".equals(type) ? t.getResponseDeadline() : t.getResolveDeadline();

            String subject = String.format("%s 工单%s预警：%s", urgentPrefix, action, t.getSerialNo());

            String content = String.format(
                    "尊敬的 %s：\n\n" +
                            "您负责的工单「%s」(ID: %s) 需要尽快%s。\n" +
                            "当前状态：%s\n" +
                            "截止时间：%s\n" +
                            "剩余时间：%d 分钟\n\n" +
                            "请登录系统查看详情。",
                    name, t.getTitle(), t.getSerialNo(), action,
                    t.getStatus(), deadline, minutesLeft
            );

            // 调用你的 sendMail 方法
            emailService.sendMail(email, subject, content);
        }
    }
}