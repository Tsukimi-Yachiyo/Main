package com.yachiyo.UserService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // 保留你原有的valueSerializer注入
    @Autowired
    private RedisSerializer<Object> valueSerializer;

    /**
     * 响应式 RedisTemplate（替换同步版，解决Bean找不到报错）
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        // Key 序列化：和你原来一致（String）
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // 构建序列化上下文（完全沿用你的序列化规则）
        RedisSerializationContext<String, Object> serializationContext =
                RedisSerializationContext.<String, Object>newSerializationContext()
                        .key(keySerializer)            // Hash Key
                        .hashKey(keySerializer)        //普通 Key
                        .value(valueSerializer)        // Value 序列化
                        .hashValue(valueSerializer)    // Hash Value 序列化
                        .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}