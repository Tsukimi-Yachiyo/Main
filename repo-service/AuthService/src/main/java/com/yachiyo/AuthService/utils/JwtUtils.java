package com.yachiyo.AuthService.utils;

import com.yachiyo.AuthService.config.SecuritySafeToolConfig;
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

    /**
     * 生成状态安全令牌
     * @return 状态安全令牌
     */
    public String generateToken() {
        return Jwts.builder()
                .setSubject(String.valueOf(SecuritySafeToolConfig.getStatusSafeCode()))
                .setIssuedAt(new Date(System.currentTimeMillis() + 1000 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 从JWT令牌中提取用户id
     * @param token JWT令牌
     * @return 用户id
     */
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userID").toString();
    }

    /**
     * 从JWT令牌中提取手机号
     * @param token JWT令牌
     * @return 用户名
     */
    public String getNameFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("name");
    }

    /**
     * 从JWT令牌中提取唯一标识
     * @param token JWT令牌
     * @return 唯一标识
     */
    public String getUniqueCodeFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 从JWT令牌中提取Claims
     * @param token JWT令牌
     * @return Claims
     */
    public Map<String, Object> getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT令牌失效: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查JWT令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        try {
            return expirationDate.before(new Date());
        } catch (Exception e) {
            log.error("JWT令牌过期检查失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 更新JWT令牌
     * @param token JWT令牌
     * @param uniqueCode 唯一标识
     * @return 更新后的JWT令牌
     */
    public String updateToken(String token, String uniqueCode) {
        String userId = getUserIdFromToken(token);
        String name = getNameFromToken(token);
        return generateToken(Long.parseLong(userId), name, uniqueCode);
    }
}
