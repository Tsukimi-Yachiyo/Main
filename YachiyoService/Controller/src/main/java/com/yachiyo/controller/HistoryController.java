package com.yachiyo.controller;

import com.yachiyo.dto.ConversationResponse;
import com.yachiyo.dto.PromptResponse;
import com.yachiyo.result.Result;
import com.yachiyo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/history")
@RequiredArgsConstructor
@Validated
public class HistoryController {

    @Autowired
    private final HistoryService historyService;

    /**
     * 获取对话记忆
     *
     * @param id 会话id
     * @return 对话记忆
     */
    @GetMapping("/{id}")
    public Result<List<PromptResponse>> getHistory(@PathVariable String id) throws Exception {
        return historyService.getHistory(id);
    }

    /**
     * 获取会话列表
     *
     * @return 会话列表
     */
    @GetMapping("/list")
    public Result<List<ConversationResponse>> getHistoryList() throws Exception {
        return historyService.getConservationIds();
    }

    /**
     * 删除对话记忆
     *
     * @param id 会话id
     * @return 删除结果
     */
    @GetMapping("/clear/{id}")
    public Result<Boolean> clearHistory(@PathVariable String id) throws Exception {
        return historyService.clearHistory(id);
    }

    /**
     * 清空所有对话记忆
     *
     * @return 清空结果
     */
    @GetMapping("/clear/all")
    public Result<Boolean> clearAllHistory() throws Exception {
        return historyService.clearAllHistory();
    }
}
