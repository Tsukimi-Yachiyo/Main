package com.yachiyo.service.Impl;

import com.yachiyo.Config.AIConfig;
import com.yachiyo.Config.ChatConfig;
import com.yachiyo.Config.ChatMemoryHistoryToolConfig;
import com.yachiyo.Config.FastMethodConfig;
import com.yachiyo.dto.ChangeConversationTitleRequest;
import com.yachiyo.dto.ChatRequest;
import com.yachiyo.entity.Message;
import com.yachiyo.entity.User;
import com.yachiyo.mapper.ConversationMapper;
import com.yachiyo.mapper.UserMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.ChatService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service @Slf4j
public class ChatServiceImpl implements ChatService {

    @Resource(name = "ChatModel")
    private ChatClient chatClient;

    @Autowired
    private ChatConfig chatConfig;

    @Autowired
    private ChatMemoryHistoryToolConfig chatMemoryHistoryToolConfig;

    @Autowired
    private FastMethodConfig fastMethodConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChatMemory chatMemory;

    @Value("${chat.system}")
    private String ChatPrompt;


    @Override
    public Result<String> Chat(ChatRequest chatRequest) {
        String message = chatRequest.getMessage();

        String systemPrompt = ChatCommon(chatRequest) ;
        if (systemPrompt == null) {
            systemPrompt = ChatPrompt;
        }

        String response = chatClient.prompt()
                .user(message)
                .system(systemPrompt)
                .advisors(advisor -> advisor.param(CONVERSATION_ID, Long.parseLong(chatRequest.getConversationId())))
                .call()
                .content();
        return Result.success(response);
    }

    @Override
    public Result<String> Create() {
        try {
            Long id = chatMemoryHistoryToolConfig.create();
            return Result.success(String.valueOf(id));
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return Result.error("500", "创建会话失败");
        }
    }

    @Override
    public SseEmitter StreamChat(ChatRequest chatRequest) {
        String message = chatRequest.getMessage();

        String systemPrompt = ChatCommon(chatRequest) ;
        if (systemPrompt == null) {
            systemPrompt = ChatPrompt;
        }

        // 创建SseEmitter
        SseEmitter emitter = new SseEmitter(0L);

        try{
            String finalSystemPrompt = systemPrompt;
            CompletableFuture.runAsync(() -> {try {

                chatClient.prompt()
                    .user(message)
                    .system(finalSystemPrompt)
                    .advisors(advisor -> advisor.param(CONVERSATION_ID, Long.parseLong(chatRequest.getConversationId())))
                    .stream()
                    .content()
                    .doOnNext(response -> {
                        try {
                            emitter.send(SseEmitter.event().data(response));
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                            log.error("发送SSE事件失败", e);
                        }
                    })
                    .doOnComplete(() -> {
                        try {
                            emitter.send(SseEmitter.event().data("[DONE]"));
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
        } catch (Exception e) {
            emitter.completeWithError(e);
            log.error("流式聊天失败", e);
        }
        return emitter;
    }

    @Override
    public Result<Boolean> ChangeConversationTitle(ChangeConversationTitleRequest changeConversationTitleRequest) {
        try {
            int conversationId = Integer.parseInt(String.valueOf(changeConversationTitleRequest.getConversationId()));
            chatMemoryHistoryToolConfig.changeTitle(conversationId, changeConversationTitleRequest.getTitle());
            return Result.success(true);
        } catch (Exception e) {
            log.error("修改会话标题失败", e);
            return Result.error("500", "修改会话标题失败");
        }
    }


    private String ChatCommon(ChatRequest chatRequest) {
        String conversationId = chatRequest.getConversationId();
        String message = chatRequest.getMessage();
        // 检查会话是否存在
        try {
            chatMemoryHistoryToolConfig.save(conversationId, message);
        } catch (Exception e) {
            log.error("保存对话记忆失败", e);
            return null;
        }
        if (chatMemory.get(conversationId).isEmpty()) {
            List<String> systemMessage = chatConfig.getSystemMessage();
            List<String> userMessage = chatConfig.getUserMessage();
            // 添加预设对话
            for(int i = 0; i < systemMessage.size(); i++) {
                chatMemory.add(conversationId, new SystemMessage(systemMessage.get(i)));
                chatMemory.add(conversationId, new UserMessage(userMessage.get(i)));
            }
        }

        try {
            Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            boolean isBirthday = redisTemplate.opsForHash().get("user:" + userId, "birthday").equals("true");
            if(isBirthday) {
                ChatPrompt += "今天是用户的生日，";
            }
        } catch (Exception e) {
            log.error("获取生日或节日失败", e);
            return null;
        }
        // 从Redis中获取节日
        try {
            String holiday = fastMethodConfig.getHoliday();
            if(!Objects.equals(holiday, null)) {
                ChatPrompt += "今天是" + holiday + "，";
            }
        } catch (Exception e) {
            log.error("获取节日失败", e);
            return null;
        }

        return ChatPrompt;
    }
}



