package com.yachiyo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeConversationTitleRequest {

    private Long conversationId;

    @NotBlank(message = "标题不能为空")
    private String title;
}
