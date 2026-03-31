package com.yachiyo.service;

import com.yachiyo.dto.ConversationResponse;
import com.yachiyo.dto.PromptResponse;
import com.yachiyo.result.Result;

import java.util.List;

public interface HistoryService {

    /**
     * 获取会话记忆
     * @param conservationId 会话ID
     * @return Result<List<PromptRequest>>
     */
    Result<List<PromptResponse>> getHistory(String conservationId);

    /**
     * 获取会话列表
     *
     * @return Result<List<Integer>>
     */
    Result<List<ConversationResponse>> getConservationIds();

     /**
     * 清空对话记忆
     *
     * @param conservationId 会话ID
     * @return Result<Boolean>
     */
    Result<Boolean> clearHistory(String conservationId);

    /**
     * 清空所有对话记忆
     *
     * @return Result<Boolean>
     */
    Result<Boolean> clearAllHistory();
}
