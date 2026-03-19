package com.yachiyo.service;

import com.yachiyo.dto.ChangeConversationTitleRequest;
import com.yachiyo.dto.ChatRequest;
import com.yachiyo.result.Result;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {

    /**
     * 聊天
     * @param chatRequest 聊天请求
     * @return 回复
     */
    @Schema(description = "聊天请求")
    public Result<String> Chat(ChatRequest chatRequest);

     /**
     * 创建会话
     * @return 会话ID
     */
    @Schema(description = "创建会话")
    public Result<String> Create();


    /**
     * 流式聊天
     * @param chatRequest 聊天请求
     * @return 回复
     */
     @Schema(description = "流式聊天")
    public SseEmitter StreamChat(ChatRequest chatRequest);

     /**
     * 修改会话标题
     * @param changeConversationTitleRequest 修改会话标题请求
     * @return 修改结果
     */
    @Schema(description = "修改会话标题")
    public Result<Boolean> ChangeConversationTitle(ChangeConversationTitleRequest changeConversationTitleRequest);
}
