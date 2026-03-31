package com.yachiyo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TTSRequest {

    @NotBlank(message = "文本不能为空")
    private String text;

    @NotBlank(message = "文本语言不能为空")
    private String text_language;
}
