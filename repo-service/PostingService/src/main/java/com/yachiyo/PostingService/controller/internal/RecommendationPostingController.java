package com.yachiyo.PostingService.controller.internal;

import com.yachiyo.PostingService.service.RecommendationPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/posting")
@RequiredArgsConstructor
@Validated
public class RecommendationPostingController {

    private final RecommendationPostingService recommendationPostingService;

    @RequestMapping("/recommend")
    public void recommend() {
        recommendationPostingService.recommendPosting();
    }
}
