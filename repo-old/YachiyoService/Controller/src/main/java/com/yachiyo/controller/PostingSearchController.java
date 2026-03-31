package com.yachiyo.controller;

import com.yachiyo.result.Result;
import com.yachiyo.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/searching")
@RequiredArgsConstructor
@Validated
public class PostingSearchController {

    private final PostingService postingService;

    /**
     * 搜索帖子
     */
    @PostMapping("/search")
    public Result<List<Integer>> searchPosting(@RequestParam String keyword) {
        return postingService.searchPosting(keyword);
    }

    /**
     * 点赞的帖子
     */
    @PostMapping("/like")
    public Result<List<Integer>> likePosting() {
        return postingService.getLikePosting();
    }

    /**
     * 收藏的帖子
     */
    @PostMapping("/collection")
    public Result<List<Integer>> collectionPosting() {
        return postingService.getCollectionPosting();
    }
}
