package com.yachiyo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MailRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "接收人不能为空")
    private Long receiverId;

    private Boolean isSpecial;
}
