package com.yachiyo.controller;

import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.UploadPostingRequest;
import com.yachiyo.result.Result;
import com.yachiyo.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/posting")
@RequiredArgsConstructor
@Validated
public class PostingController {

    private final PostingService postingService;

    /**
     * 上传帖子
     */
    @PostMapping("/upload")
    public Result<Boolean> uploadPosting(@Validated @RequestBody UploadPostingRequest request) {
        return postingService.uploadPosting(request);
    }

    /**
     * 获取帖子
     */
    @PostMapping("/get")
    public Result<GetPostingResponse> getPosting(@RequestParam int postingId) {
        return postingService.getPosting(postingId);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/like")
    public Result<Boolean> likePosting(@RequestParam int postingId) {
        return postingService.likePosting(postingId);
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/collection")
    public Result<Boolean> collectionPosting(@RequestParam int postingId) {
        return postingService.collectionPosting(postingId);
    }

    /**
     * 取消点赞帖子
     */
    @PostMapping("/cancelLike")
    public Result<Boolean> cancelLikePosting(@RequestParam int postingId) {
        return postingService.cancelLikePosting(postingId);
    }

    /**
     * 取消收藏帖子
     */
    @PostMapping("/cancelCollection")
    public Result<Boolean> cancelCollectionPosting(@RequestParam int postingId) {
        return postingService.cancelCollectionPosting(postingId);
    }

    /**
     * 删除帖子
     */
    @PostMapping("/delete")
    public Result<Boolean> deletePosting(@RequestParam int postingId) {
        return postingService.deletePosting(postingId);
    }
}
