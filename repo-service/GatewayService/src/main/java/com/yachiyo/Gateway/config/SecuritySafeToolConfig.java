package com.yachiyo.Gateway.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 安全工具类
 * 该类提供了一些安全相关的工具方法，例如密码加密、解密等。
 */
@Component
@Slf4j
public class SecuritySafeToolConfig {

    @Getter
    private static final int statusSafeCode = new Random().nextInt(Integer.MAX_VALUE);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public boolean checkUnique(int userId, String unique) {
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        Object redisUniqueObj = hashOps.get("user:" + userId, "unique");
        if (redisUniqueObj == null) {
            return false;
        }
        return unique.equals(redisUniqueObj.toString());
    }


}
