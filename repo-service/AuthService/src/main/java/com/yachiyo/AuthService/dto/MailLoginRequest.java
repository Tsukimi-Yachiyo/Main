package com.yachiyo.AuthService.dto;

import lombok.Data;

@Data
public class MailLoginRequest {

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;
}
