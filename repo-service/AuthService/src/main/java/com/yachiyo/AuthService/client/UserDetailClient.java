package com.yachiyo.AuthService.client;

import com.yachiyo.AuthService.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserDetailClient {

    @GetMapping("/internal/user/detail/init/{id}")
    Result<Boolean> initUserDetail(@PathVariable Long id);

    @GetMapping("/internal/user/detail/login/{id}")
    Result<Boolean> login(@PathVariable Long id);
}
