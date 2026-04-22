package com.yachiyo.Gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

    @Override @NullMarked
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求路径: {}, 方法: {}, 来源IP: {}",
                request.getPath(), request.getMethod(),
                request.getRemoteAddress());

        long startTime = System.currentTimeMillis();
        return chain.filter(exchange).doFinally(signalType -> {
            long duration = System.currentTimeMillis() - startTime;
            HttpStatus status = (HttpStatus) exchange.getResponse().getStatusCode();
            log.info("响应状态: {}, 耗时: {}ms", status, duration);
        });
    }

    @Override
    public int getOrder() {
        return -2; // 在鉴权过滤器(-1)之后执行
    }
}