package com.yachiyo.service.Impl;

import com.yachiyo.Config.ChatMemoryHistoryToolConfig;
import com.yachiyo.dto.ChatRequest;
import com.yachiyo.mapper.ConversationMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.ChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource(name = "ChatModel")
    private ChatClient chatClient;

    @Autowired
    private ChatMemoryHistoryToolConfig chatMemoryHistoryToolConfig;

    @Override
    public Result<String> Chat(ChatRequest chatRequest) {
        String conservationId = chatRequest.getConservationId();
        String message = chatRequest.getMessage();
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        // 保存会话记忆
        try {
            chatMemoryHistoryToolConfig.save(Integer.parseInt(conservationId), message, response);
        } catch (Exception e) {
            log.error("保存对话记忆失败", e);
            return Result.error("500", "保存对话记忆失败");
        }
        return Result.success(response);
    }

    @Override
    public Result<String> Create() {
        try {
            int id = chatMemoryHistoryToolConfig.create();
            return Result.success(String.valueOf(id));
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return Result.error("500", "创建会话失败");
        }
    }
}
