package com.yachiyo.PostingService.controller;

import com.yachiyo.PostingService.dto.InteractionRequest;
import com.yachiyo.PostingService.result.Result;
import com.yachiyo.PostingService.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/posting")
@RequiredArgsConstructor
@Validated
public class OperatePublicPostingController {

    private final PostingService postingService;

    /**
     * 处理帖子互动（点赞/收藏）
     */
    @PostMapping("/interaction")
    public Result<Boolean> handleInteraction(@RequestBody InteractionRequest request) {
        return postingService.handleInteraction(request);
    }
}
