package com.yachiyo.controller;

import com.yachiyo.dto.LoginRequest;
import com.yachiyo.dto.RegisterRequest;
import com.yachiyo.result.Result;
import com.yachiyo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;
    /**
     * 登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<String> Login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.Login(loginRequest);
    }

    /**
     * 注册
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<String> Register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.Register(registerRequest);
    }

    /**
     * 发送验证码
     * @param email 邮箱
     * @return 发送结果
     */
    @PostMapping("/send-code")
    public Result<Boolean> SendCode(@RequestBody @Valid String email) {
        return authService.SendCode(email);
    }
}
