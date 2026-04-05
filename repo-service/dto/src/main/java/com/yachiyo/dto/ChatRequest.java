package com.yachiyo.dto;

import com.yachiyo.tool.SensitiveWordFilter;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {

    @NotBlank(message = "消息不能为空")
    @SensitiveWordFilter(message = "消息包含敏感词")
    private String message;

    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

}
