package org.freeman.ticketmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.freeman.ticketmanager.entity.TicketLog;

import java.util.List;

@Mapper
public interface TicketLogMapper {

    /**
     * 插入日志
     */
    int insert(TicketLog ticketLog);

    /**
     * 查询日志列表
     * 支持根据 id, ticketId, operatorId 动态过滤
     */
    List<TicketLog> selectList(TicketLog query);
}