package com.yachiyo.dto;

import com.yachiyo.tool.SensitiveWordFilter;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeConversationTitleRequest {

    private Long conversationId;

    @NotBlank(message = "标题不能为空")
    @SensitiveWordFilter(message = "标题包含敏感词")
    private String title;
}
