package com.yachiyo.service.Impl;

import com.yachiyo.Config.ChatMemoryHistoryToolConfig;
import com.yachiyo.Config.FastMethodConfig;
import com.yachiyo.dto.ChatRequest;
import com.yachiyo.entity.User;
import com.yachiyo.mapper.ConversationMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.ChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service @Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource(name = "ChatModel")
    private ChatClient chatClient;

    @Autowired
    private ChatMemoryHistoryToolConfig chatMemoryHistoryToolConfig;

    @Autowired
    private FastMethodConfig fastMethodConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<String> Chat(ChatRequest chatRequest) {
        String conversationId = chatRequest.getConversationId();
        String message = chatRequest.getMessage();
        // 检查会话是否存在
        try {
            chatMemoryHistoryToolConfig.save(Integer.parseInt(conversationId), message);
        } catch (Exception e) {
            log.error("保存对话记忆失败", e);
            return Result.error("500", "保存对话记忆失败");
        }
        String response = chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(CONVERSATION_ID, conversationId))
                .call()
                .content();
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

    @Override
    public SseEmitter StreamChat(ChatRequest chatRequest) {
        String conversationId = chatRequest.getConversationId();
        String message = chatRequest.getMessage();
        // 检查会话是否存在
        try {
            chatMemoryHistoryToolConfig.save(Integer.parseInt(conversationId), message);
        } catch (Exception e) {
            log.error("保存对话记忆失败", e);
            return null;
        }

        String systemMessage = "";
        try {
            int userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            boolean isBirthday = redisTemplate.opsForHash().get("user:" + userId, "birthday").equals("true");
            if(isBirthday) {
                systemMessage += "今天是用户的生日，";
            }
        } catch (Exception e) {
            log.error("获取生日或节日失败", e);
            return null;
        }
        // 从Redis中获取节日
        try {
            String holiday = fastMethodConfig.getHoliday();
            if(!Objects.equals(holiday, null)) {
                systemMessage += "今天是" + holiday + "，";
            }
        } catch (Exception e) {
            log.error("获取节日失败", e);
            return null;
        }

        // 创建SseEmitter
        SseEmitter emitter = new SseEmitter(0L);

        String finalSystemMessage = systemMessage.isEmpty() ? "无特殊事件" : systemMessage;

        CompletableFuture.runAsync(() -> {try {

            chatClient.prompt()
                    .user(message)
                    .system(finalSystemMessage)
                    .advisors(advisor -> advisor.param(CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .doOnNext(response -> {
                        try {
                            emitter.send(response);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                            log.error("发送SSE事件失败", e);
                        }
                    })
                    .doOnComplete(() -> {
                        try {
                            emitter.send("[DONE]");
                            emitter.complete();
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                            log.error("完成SSE事件失败", e);
                        }
                    })
                    .doOnError(error -> {
                        try {
                            emitter.completeWithError(error);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                            log.error("错误SSE事件失败", e);
                        }
                    })
                    .subscribe(
                            null,
                            emitter::completeWithError,
                            () -> log.info("流式聊天完成")
                    );
                } catch (Exception e) {
                    emitter.completeWithError(e);
                    log.error("流式聊天失败", e);
                }
        });
        return emitter;
    }
}