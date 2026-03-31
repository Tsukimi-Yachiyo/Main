package com.yachiyo.service.Impl;

import com.yachiyo.Config.ChatMemoryHistoryToolConfig;
import com.yachiyo.dto.ConversationResponse;
import com.yachiyo.dto.PromptResponse;
import com.yachiyo.result.Result;
import com.yachiyo.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private ChatMemoryHistoryToolConfig chatMemoryHistoryToolConfig;

    @Override
    public Result<List<PromptResponse>> getHistory(String conservationId) {
        try {
            return Result.success(chatMemoryHistoryToolConfig.getHistory(Long.parseLong(conservationId)));
        } catch (Exception e) {
            log.error("获取会话记忆失败", e);
            return Result.error("获取会话记忆失败");
        }
    }

    @Override
    public Result<List<ConversationResponse>> getConservationIds() {
        try {
            return Result.success(chatMemoryHistoryToolConfig.getConservationIds());
        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            return Result.error("获取会话列表失败");
        }
    }

    @Override
    public Result<Boolean> clearHistory(String conservationId) {
        try {
            chatMemoryHistoryToolConfig.clearHistory(Long.parseLong(conservationId));
            return Result.success(true);
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return Result.error("500","删除会话失败", e.getMessage());
        }
    }

    @Override
    public Result<Boolean> clearAllHistory() {
        try {
            chatMemoryHistoryToolConfig.clearAllHistory();
            return Result.success(true);
        } catch (Exception e) {
            log.error("清空所有会话记忆失败", e);
            return Result.error("清空所有会话记忆失败");
        }
    }
}
