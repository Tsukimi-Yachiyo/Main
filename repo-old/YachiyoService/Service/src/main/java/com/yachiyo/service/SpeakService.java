package com.yachiyo.service;

import com.yachiyo.dto.SpeakRequest;
import io.swagger.v3.oas.annotations.media.Schema;

public interface SpeakService {

    /**
     * 文本合成语音方法
     * @param speakRequest 待合成语音的文本
     * @return 合成后的语音文件路径
     */
    @Schema(description = "待合成语音的文本")
    byte[] TextToSpeech(SpeakRequest speakRequest);

}
