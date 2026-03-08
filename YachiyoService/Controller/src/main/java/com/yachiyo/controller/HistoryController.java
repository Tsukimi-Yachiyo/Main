package com.yachiyo.controller;

import com.yachiyo.dto.PromptRequest;
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
    public Result<List<PromptRequest>> getHistory(@PathVariable String id) throws Exception {
        return historyService.getHistory(id);
    }

    /**
     * 获取会话列表
     *
     * @return 会话列表
     */
    @GetMapping("/list")
    public Result<List<Integer>> getHistoryList() throws Exception {
        return historyService.getConservationIds();
    }
}
