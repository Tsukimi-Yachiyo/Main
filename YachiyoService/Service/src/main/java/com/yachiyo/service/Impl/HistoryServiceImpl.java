package com.yachiyo.service.Impl;

import com.yachiyo.Config.ChatMemoryHistoryToolConfig;
import com.yachiyo.dto.PromptRequest;
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
    public Result<List<PromptRequest>> getHistory(String conservationId) {
        try {
            return Result.success(chatMemoryHistoryToolConfig.getHistory(Integer.parseInt(conservationId)));
        } catch (Exception e) {
            log.error("获取会话记忆失败", e);
            return Result.error("获取会话记忆失败");
        }
    }

    @Override
    public Result<List<Integer>> getConservationIds() {
        try {
            return Result.success(chatMemoryHistoryToolConfig.getConservationIds());
        } catch (Exception e) {
            log.error("获取会话列表失败", e);
            return Result.error("获取会话列表失败");
        }
    }
}
