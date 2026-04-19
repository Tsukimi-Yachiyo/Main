package com.yachiyo.UserService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 自定义 Jackson3 序列化器，用于 Redis 缓存
 * 该序列化器使用 Jackson3 进行序列化和反序列化，支持 Spring Cache 缓存注解。
 * 该序列化器可以在 Spring Boot 应用中配置为默认的 Redis 序列化器，
 * 也可以在需要自定义序列化器的地方进行配置。
 */
@Slf4j
@Component
public class GenericJackson3JsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper;

    @Autowired
    public GenericJackson3JsonRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override @SuppressWarnings("All")
    public byte[] serialize (Object object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsString(object).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("不能序列化: " + e.getMessage(), e);
            throw new SerializationException("不能序列化: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            String json = new String(bytes, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.error("不能反序列化: {}", e.getMessage(), e);
            throw new SerializationException("不能反序列化: " + e.getMessage(), e);
        }
    }
}
