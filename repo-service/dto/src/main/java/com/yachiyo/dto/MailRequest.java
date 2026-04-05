package com.yachiyo.dto;

import com.yachiyo.tool.SensitiveWordFilter;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MailRequest {

    @NotBlank(message = "标题不能为空")
    @SensitiveWordFilter(message = "标题包含敏感词")
    private String title;

    @NotBlank(message = "内容不能为空")
    @SensitiveWordFilter(message = "内容包含敏感词")
    private String content;

    @NotBlank(message = "接收人不能为空")
    private Long receiverId;

    private Boolean isSpecial;
}
