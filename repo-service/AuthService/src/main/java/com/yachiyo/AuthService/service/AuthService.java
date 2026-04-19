package com.yachiyo.AuthService.service;

import com.yachiyo.AuthService.dto.LoginRequest;
import com.yachiyo.AuthService.dto.MailLoginRequest;
import com.yachiyo.AuthService.dto.RegisterRequest;
import com.yachiyo.AuthService.result.Result;

public interface AuthService {

    /**
     * 登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    Result<String> Login(LoginRequest loginRequest);

    /**
     * 注册
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    Result<String> Register(RegisterRequest registerRequest);

    /**
     * 发送验证码
     * @param email 邮箱
     * @return 发送结果
     */
    Result<Boolean> SendCode(String email);

    /**
     * 更改密码
     * @param registerRequest 更改密码请求
     * @return 更改密码结果
     */
    Result<Boolean> ChangePassword(RegisterRequest registerRequest);

    /**
     * 退出登录
     * @return 退出登录结果
     */
    Result<Boolean> Logout();

    /**
     * 邮箱登录
     * @param mailLoginRequest 登录邮箱登录请求
     * @return 登录结果
     */
    Result<String> LoginByEmail(MailLoginRequest mailLoginRequest);

    /**
     * 刷新令牌
     * @return 刷新令牌结果
     */
    Result<String> RefreshToken(String token, Long userId);
}
