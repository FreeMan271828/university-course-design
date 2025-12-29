package org.freeman.ticketmanager.service;

import org.freeman.ticketmanager.entity.SlaPolicy;
import org.freeman.ticketmanager.mapper.SlaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlaService {
    @Autowired
    private SlaMapper slaMapper;

    public List<SlaPolicy> findAll() {
        return slaMapper.selectAll();
    }

    // 新增或修改时，清除该优先级的缓存
    @Transactional
    @CacheEvict(value = "config:sla", key = "#sla.priority")
    public void saveOrUpdate(SlaPolicy sla) {
        if (slaMapper.update(sla) == 0) {
            slaMapper.insert(sla);
        }
    }

    // 删除时清除缓存
    @Transactional
    @CacheEvict(value = "config:sla", key = "#priority")
    public void delete(String priority) {
        slaMapper.delete(priority);
    }
}