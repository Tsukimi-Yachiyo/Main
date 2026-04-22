package com.yachiyo.CoinService.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CurrentUserIdProvider {
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return (Long.valueOf((String) authentication.getPrincipal()));
        } else {
            throw new IllegalStateException("未获取到登录用户信息");
        }
    }
}
