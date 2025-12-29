package org.freeman.ticketmanager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.freeman.ticketmanager.entity.SlaPolicy;

import java.util.List;

@Mapper
public interface SlaMapper {
    List<SlaPolicy> selectAll();
    SlaPolicy selectByPriority(String priority);
    int insert(SlaPolicy sla);
    int update(SlaPolicy sla);
    int delete(String priority);
}