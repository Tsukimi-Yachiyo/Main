package com.yachiyo.controller;

import com.yachiyo.dto.ChangeConversationTitleRequest;
import com.yachiyo.dto.ChatRequest;
import com.yachiyo.dto.SpeakRequest;
import com.yachiyo.dto.TTSRequest;
import com.yachiyo.result.Result;
import com.yachiyo.service.ChatService;
import com.yachiyo.service.SpeakService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.util.Random;

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
    public Result<byte[]> Speak(@RequestBody @Valid SpeakRequest speakRequest){
        return Result.success(speakService.TextToSpeech(speakRequest), "语音合成成功", speakRequest.getText());
    }

    /**
     * 创建会话
     */
    @PostMapping("/create")
    public Result<String> Create(){
        return chatService.Create();
    }

    /**
     * 修改会话标题
     * @param changeConversationTitleRequest 修改会话标题请求
     * @return 修改结果
     */
    @PostMapping("/title")
    public Result<Boolean> ChangeConversationTitle(@RequestBody @Valid ChangeConversationTitleRequest changeConversationTitleRequest){
        return chatService.ChangeConversationTitle(changeConversationTitleRequest);
    }

    /**
     * 流式聊天
     * @param chatRequest 聊天请求
     * @return 回复
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter StreamChat(@RequestBody @Valid ChatRequest chatRequest){
        return chatService.StreamChat(chatRequest);
    }
}
