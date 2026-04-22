package com.yachiyo.AdminService.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT令牌
     * @param userId 用户id
     * @param name 用户名
     * @param uniqueCode 唯一标识
     * @return JWT令牌
     */
    public String generateToken(Long userId, String name, String uniqueCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userID", userId);
        claims.put("name", name);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(uniqueCode)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .signWith(getSignKey())
                .compact();
    }

}
