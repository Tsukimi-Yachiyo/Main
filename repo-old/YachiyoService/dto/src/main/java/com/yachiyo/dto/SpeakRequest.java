package com.yachiyo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SpeakRequest {

    @Schema(description = "待合成语音的文本")
    private String text;
}
