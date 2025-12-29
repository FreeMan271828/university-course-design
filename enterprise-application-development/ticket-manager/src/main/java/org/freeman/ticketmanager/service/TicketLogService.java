package org.freeman.ticketmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.freeman.ticketmanager.common.UserContext;
import org.freeman.ticketmanager.entity.TicketLog;
import org.freeman.ticketmanager.mapper.TicketLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketLogService {

    @Value("${isTest}")
    private String isTest;

    @Autowired
    private TicketLogMapper logMapper;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async // 可选：异步记录日志不阻塞主流程
    public void record(Long ticketId, String action, Object detailsObj) {
        TicketLog log = new TicketLog();
        if(Objects.equals(isTest, "true"))
            log.setOperatorId(1L);
        else
            log.setOperatorId(UserContext.getUserId());
        log.setTicketId(ticketId);
        log.setAction(action);

        try {
            // 将对象转为 Map 以便 TypeHandler 处理为 JSONB
            Map map = objectMapper.convertValue(detailsObj, Map.class);
            log.setDetails(map);
        } catch (Exception e) {
            // 简单容错
        }
        logMapper.insert(log);
    }

    public List<TicketLog> searchLogs(TicketLog query) {
        List<Long>ids = userService.getUserIdsByName(query.getOperatorName());
        List<TicketLog> ret = new ArrayList<>();
        for(Long id : ids){
            query.setOperatorId(id);
            List<TicketLog> logs = logMapper.selectList(query);
            ret.addAll(logs);
        }
        return ret;
    }
}