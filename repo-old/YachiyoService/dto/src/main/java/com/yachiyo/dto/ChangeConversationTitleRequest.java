package com.yachiyo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeConversationTitleRequest {

    @NotBlank(message = "对话ID不能为空")
    private int conversationId;

    @NotBlank(message = "标题不能为空")
    private String title;
}
