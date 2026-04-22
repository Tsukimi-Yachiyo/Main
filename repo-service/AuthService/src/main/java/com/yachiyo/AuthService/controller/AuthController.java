package com.yachiyo.AuthService.controller;

import com.yachiyo.AuthService.dto.LoginRequest;
import com.yachiyo.AuthService.dto.MailLoginRequest;
import com.yachiyo.AuthService.dto.RegisterRequest;
import com.yachiyo.AuthService.result.Result;
import com.yachiyo.AuthService.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public Result<Boolean> SendCode(@RequestParam @Valid String email) {
        return authService.SendCode(email);
    }

    /**
     * 更改密码
     * @param registerRequest 更改密码请求
     * @return 更改密码结果
     */
    @PostMapping("/change-password")
    public Result<Boolean> ChangePassword(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.ChangePassword(registerRequest);
    }

    /**
     * 退出登录
     * @return 退出登录结果
     */
    @PostMapping("/logout")
    public Result<Boolean> Logout() {
        return authService.Logout();
    }

    /**
     * 邮箱登录
     * @param mailLoginRequest 登录邮箱登录请求
     * @return 登录结果
     */
    @PostMapping("/login-by-email")
    public Result<String> LoginByEmail(@RequestBody @Valid MailLoginRequest mailLoginRequest) {
        return authService.LoginByEmail(mailLoginRequest);
    }

    /**
     * 刷新令牌
     * @return 刷新令牌结果
     */
    @PostMapping("/refresh-token")
    public Result<String> RefreshToken(@RequestParam String refreshToken, @RequestParam Long userId) {
        return authService.RefreshToken(refreshToken, userId);
    }
}
