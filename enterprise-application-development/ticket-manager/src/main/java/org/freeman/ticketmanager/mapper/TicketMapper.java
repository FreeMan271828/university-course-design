package org.freeman.ticketmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.freeman.ticketmanager.entity.SlaPolicy;
import org.freeman.ticketmanager.entity.Ticket;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TicketMapper {
    int insert(Ticket ticket);
    int updateStatus(Ticket ticket, LocalDateTime responseAt, LocalDateTime resolveAt);
    int updateAssignee(Ticket ticket, Long assigneeId);
    List<Ticket> selectList(@Param("creatorId") Long creatorId,
                            @Param("assigneeId") Long assigneeId,
                            @Param("status") String status,
                            @Param("categoryId") Long categoryId);
    SlaPolicy selectSlaPolicy(String priority);
    Ticket selectById(Long id);
    List<Ticket> selectApproachingTickets(@Param("responseThreshold") LocalDateTime responseThreshold,
                                          @Param("resolveThreshold") LocalDateTime resolveThreshold);
}

