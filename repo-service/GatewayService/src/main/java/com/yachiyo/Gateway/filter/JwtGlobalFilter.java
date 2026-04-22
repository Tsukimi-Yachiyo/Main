package com.yachiyo.Gateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import com.yachiyo.Gateway.config.SecuritySafeToolConfig;
import com.yachiyo.Gateway.config.YamlConfigProperties;
import com.yachiyo.Gateway.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;
    private final YamlConfigProperties yamlConfigProperties;
    private final SecuritySafeToolConfig securitySafeToolConfig;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override @NullMarked
    public Mono<Void> filter( ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. 白名单路径直接放行
        if (isWhiteListPath(path)) {
            log.debug("请求路径 [{}] 在白名单中，直接放行", path);
            return chain.filter(exchange);
        }

        // 2. 提取 Token
        String token = extractJwtFromRequest(request);
        if (token == null) {
            log.warn("请求路径 [{}] 未携带 Token", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "未认证");
        }

        // 3. 验证 Token 有效性
        if (!jwtUtils.isTokenValid(token)) {
            log.warn("请求路径 [{}] 携带的 Token 无效", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "无效的令牌");
        }

        // 4. 验证 Token 是否过期
        if (jwtUtils.isTokenExpired(token)) {
            log.warn("请求路径 [{}] 携带的 Token 已过期", path);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "令牌过期");
        }

        // 5. 验证唯一码
        String userIdStr = jwtUtils.getUserIdFromToken(token);
        String uniqueCode = jwtUtils.getUniqueCodeFromToken(token);
        try {
            int userId = Integer.parseInt(userIdStr);
            if (!securitySafeToolConfig.checkUnique(userId, uniqueCode)) {
                log.warn("请求路径 [{}] 唯一码验证失败，userId: {}", path, userId);
                return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "实时唯一码错误");
            }
        } catch (NumberFormatException e) {
            log.error("Token 中的 userId 格式错误: {}", userIdStr);
            return sendErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "令牌信息异常");
        }

        // 6. 验证通过，解析用户信息并传递给下游服务
        try {
            String userId = userIdStr;
            String username = jwtUtils.getNameFromToken(token);

            // 分配角色（与原逻辑一致）
            String role = "0".equals(userId) ? "ROLE_ADMIN" : "ROLE_USER";

            // 构造新的请求，添加自定义 Header 传递给下游
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Name", username)
                    .header("X-User-Role", role)
                    .header("X-Auth-Token", token)
                    .build();

            log.debug("用户 [{}] (ID: {}) 认证通过，角色: {}", username, userId, role);

            // 将修改后的请求传递给下游
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("解析 Token 信息时发生异常", e);
            return sendErrorResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "服务器错误");
        }
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        for (String openUrl : yamlConfigProperties.getOpenApi()) {
            if (pathMatcher.match(openUrl, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求头中提取 JWT Token
     */
    private String extractJwtFromRequest(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 发送标准 JSON 错误响应
     */
    private Mono<Void> sendErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(new IllegalStateException("Response already committed"));
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 构造与原代码风格一致的错误 JSON
        String body = String.format("{\"error\": \"%d\", \"message\": \"%s\"}", status.value(), message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}