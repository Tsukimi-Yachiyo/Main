package com.yachiyo.PostingService.controller.internal;

import com.yachiyo.PostingService.service.RecommendationPostingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/posting")
@RequiredArgsConstructor
@Validated
public class RecommendationPostingController {

    private final RecommendationPostingService recommendationPostingService;

    @PostMapping("/recommend")
    public void recommend() {
        recommendationPostingService.recommendPosting();
    }
}
