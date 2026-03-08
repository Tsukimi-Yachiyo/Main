package com.yachiyo.controller;

import com.yachiyo.dto.ChatRequest;
import com.yachiyo.dto.SpeakRequest;
import com.yachiyo.dto.TTSRequest;
import com.yachiyo.result.Result;
import com.yachiyo.service.ChatService;
import com.yachiyo.service.SpeakService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v2/ai")
@RequiredArgsConstructor
@Validated
public class AIChatController {

    @Autowired
    private SpeakService speakService;

    @Autowired
    private ChatService chatService;

    /**
     * 与大模型对话
     */
    @PostMapping("/chat")
    public Result<String> Chat(@RequestBody @Valid ChatRequest chatRequest) {
        return chatService.Chat(chatRequest);
    }

    /**
     * 文本转语音
     */
    @PostMapping("/speak")
    public byte[] Speak(@RequestBody @Valid SpeakRequest speakRequest){
        return speakService.TextToSpeech(speakRequest);
    }

    /**
     * 创建会话
     */
    @PostMapping("/create")
    public Result<String> Create(){
        return chatService.Create();
    }
}
