package com.yachiyo.AuthService.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    public String getUnique(Long userId) {
        Random rand = new Random();
        int randomNum = rand.nextInt(Integer.MAX_VALUE);
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        hashOps.put("user:" + userId, "unique", String.valueOf(randomNum));
        redisTemplate.expire("user:" + userId, 3600, TimeUnit.SECONDS);
        return String.valueOf(randomNum);
    }

    public String md5(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

// ... 你的其他代码

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ==================== 开发环境推荐（快速解决）===================
        configuration.setAllowedOriginPatterns(List.of("*"));           // 允许所有域名（开发最方便）
        // 或者精确写你的前端域名（推荐生产环境）：
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "http://localhost:5173",           // Vite 默认端口
        //     "http://127.0.0.1:5173",
        //     "https://你的生产前端域名.com"
        // ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));            // 允许所有请求头（开发最方便）
        // 生产环境可精确控制：
        // configuration.setAllowedHeaders(List.of(
        //     "Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin"
        // ));

        configuration.setAllowCredentials(false);   // send-code 接口不需要 cookie，设 false 更安全
        // 如果你后面要带 Authorization 头，生产环境建议设 true + 用 allowedOriginPatterns

        configuration.setMaxAge(3600L);             // 预检结果缓存 1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);   // 对所有路径生效
        return source;
    }
}
