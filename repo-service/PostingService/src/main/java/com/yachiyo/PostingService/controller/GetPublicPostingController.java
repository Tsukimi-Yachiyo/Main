package com.yachiyo.PostingService.controller;

import com.yachiyo.PostingService.dto.GetPostingResponse;
import com.yachiyo.PostingService.dto.PostEncapsulateResponse;
import com.yachiyo.PostingService.dto.PostStatsResponse;
import com.yachiyo.PostingService.result.Result;
import com.yachiyo.PostingService.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/posting")
@RequiredArgsConstructor
@Validated
public class GetPublicPostingController {


    private final PostingService postingService;

    /**
     * 获取帖子
     */
    @PostMapping("/get")
    public Result<GetPostingResponse> getPosting(@RequestParam Long postingId) {
        return postingService.getPosting(postingId);
    }

    /**
     * 获取帖子统计信息
     */
    @PostMapping("/stats")
    public Result<PostStatsResponse> getPostingStats(@RequestParam Long postingId) {
        return postingService.getPostingStats(postingId);
    }

    /**
     * 搜索帖子
     */
    @PostMapping("/search")
    public Result<List<Long>> searchPosting(@RequestParam String keyword, @RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return postingService.searchPosting(keyword, pageNum, pageSize);
    }

    /**
     * 点赞的帖子
     */
    @PostMapping("/like")
    public Result<List<Long>> likePosting() {
        return postingService.getLikePosting();
    }

    /**
     * 收藏的帖子
     */
    @PostMapping("/collection")
    public Result<List<Long>> collectionPosting() {
        return postingService.getCollectionPosting();
    }

    /**
     * 获取帖子简述
     *
     * @param postingId 帖子id
     * @return 帖子简述
     */
    @PostMapping("/encapsulate")
    public Result<PostEncapsulateResponse> getPostingEncapsulate(@RequestParam Long postingId) {
        return postingService.getPostingEncapsulate(postingId);
    }
}
