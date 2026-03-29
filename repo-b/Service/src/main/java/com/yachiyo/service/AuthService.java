package com.yachiyo.service;

import com.yachiyo.dto.LoginRequest;
import com.yachiyo.dto.MailLoginRequest;
import com.yachiyo.dto.RegisterRequest;
import com.yachiyo.result.Result;
import io.swagger.v3.oas.annotations.media.Schema;

public interface AuthService {

    /**
     * 登录
     * @param loginRequest 登录请求
     * @return 登录结果
     */
    @Schema(description = "登录请求")
    Result<String> Login(LoginRequest loginRequest);

    /**
     * 注册
     * @param registerRequest 注册请求
     * @return 注册结果
     */
    @Schema(description = "注册请求")
    Result<String> Register(RegisterRequest registerRequest);

    /**
     * 发送验证码
     * @param email 邮箱
     * @return 发送结果
     */
    @Schema(description = "发送验证码请求")
    Result<Boolean> SendCode(String email);

    /**
     * 更改密码
     * @param registerRequest 更改密码请求
     * @return 更改密码结果
     */
    @Schema(description = "更改密码请求")
    Result<Boolean> ChangePassword(RegisterRequest registerRequest);

    /**
     * 退出登录
     * @return 退出登录结果
     */
    @Schema(description = "退出登录请求")
    Result<Boolean> Logout();

    /**
     * 邮箱登录
     * @param mailLoginRequest 登录邮箱登录请求
     * @return 登录结果
     */
    @Schema(description = "邮箱登录请求")
    Result<String> LoginByEmail(MailLoginRequest mailLoginRequest);
}
