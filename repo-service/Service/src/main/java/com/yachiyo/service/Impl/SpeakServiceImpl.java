package com.yachiyo.service.Impl;

import com.yachiyo.Config.TransformConfig;
import com.yachiyo.dto.SpeakRequest;
import com.yachiyo.service.SpeakService;
import com.yachiyo.dto.TTSRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpeakServiceImpl implements SpeakService {

    @Autowired
    private TransformConfig transformConfig;

    @Autowired
    private RestTemplate restTemplate;

     @Override
    public byte[] TextToSpeech(SpeakRequest speakRequest) {
        // 将文字翻译成日语
        String japaneseText = transformConfig.translate(speakRequest.getText(), "auto", "jp");

        TTSRequest ttsRequest = new TTSRequest();
        ttsRequest.setText(japaneseText);
        ttsRequest.setText_language("ja");

        // 发送请求到http://localhost:9882, 获取response.context中的二进制语言数据
        return restTemplate.postForObject("http://tts_yachiyo.fucku.top", ttsRequest, byte[].class);
    }

}
