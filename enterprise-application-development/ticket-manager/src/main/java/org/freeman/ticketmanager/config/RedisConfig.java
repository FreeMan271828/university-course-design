package org.freeman.ticketmanager.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    // 1. 手动操作使用的 Template (存Token, 流水号)
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    // 2. Spring Cache 配置 (存 SLA 策略等配置数据)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24)); // 默认缓存一天

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}