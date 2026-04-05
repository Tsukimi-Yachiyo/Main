package com.yachiyo.dto;

import com.yachiyo.tool.SensitiveWordFilter;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @SensitiveWordFilter(message = "用户名包含敏感词")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "邮箱不能为空")
    private String email;

     @NotBlank(message = "验证码不能为空")
    private String code;
}
