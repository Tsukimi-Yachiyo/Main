package com.yachiyo.ColumnService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12)) // 这里统一设置过期
                .serializeKeysWith(RedisCacheConfiguration.defaultCacheConfig().getKeySerializationPair())
                .serializeValuesWith(RedisCacheConfiguration.defaultCacheConfig().getValueSerializationPair());

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
