package com.yachiyo.controller;

import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.UploadPostingRequest;
import com.yachiyo.result.Result;
import com.yachiyo.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public Result<Boolean> uploadPosting(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String type,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestPart(required = false) List<MultipartFile> files) {
        return postingService.uploadPosting(new UploadPostingRequest(title, content, type, coverImage, files));
    }

    /**
     * 获取帖子
     */
    @PostMapping("/get")
    public Result<GetPostingResponse> getPosting(@RequestParam Long postingId) {
        return postingService.getPosting(postingId);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/like")
    public Result<Boolean> likePosting(@RequestParam Long postingId) {
        return postingService.likePosting(postingId);
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/collection")
    public Result<Boolean> collectionPosting(@RequestParam Long postingId) {
        return postingService.collectionPosting(postingId);
    }

    /**
     * 取消点赞帖子
     */
    @PostMapping("/cancelLike")
    public Result<Boolean> cancelLikePosting(@RequestParam Long postingId) {
        return postingService.cancelLikePosting(postingId);
    }

    /**
     * 取消收藏帖子
     */
    @PostMapping("/cancelCollection")
    public Result<Boolean> cancelCollectionPosting(@RequestParam Long postingId) {
        return postingService.cancelCollectionPosting(postingId);
    }

    /**
     * 获取帖子的收藏数
     */
    @PostMapping("/getCollectionCount")
    public Result<Long> getCollectionCount(@RequestParam Long postingId) {
        return postingService.getCollectionCount(postingId);
    }

    /**
     * 获取帖子的点赞数
     */
    @PostMapping("/getLikeCount")
    public Result<Long> getLikeCount(@RequestParam Long postingId) {
        return postingService.getLikeCount(postingId);
    }

    /**
     * 获取帖子的阅读数
     */
    @PostMapping("/getReadingCount")
    public Result<Long> getReadingCount(@RequestParam Long postingId) {
        return postingService.getReadingCount(postingId);
    }

    /**
     * 获取帖子的金币数
     */
    @PostMapping("/getCoinCount")
    public Result<Long> getCoinCount(@RequestParam Long postingId) {
        return postingService.getCoinCount(postingId);
    }

    /**
     * 判断是否点赞帖子
     */
    @PostMapping("/isLiked")
    public Result<Boolean> isLiked(@RequestParam Long postingId) {
        return postingService.isLiked(postingId);
    }

    /**
     * 判断是否收藏帖子
     */
    @PostMapping("/isCollected")
    public Result<Boolean> isCollected(@RequestParam Long postingId) {
        return postingService.isCollected(postingId);
    }

    /**
     * 删除帖子
     */
    @PostMapping("/delete")
    public Result<Boolean> deletePosting(@RequestParam Long postingId) {
        return postingService.deletePosting(postingId);
    }

    /**
     * 获取自己的帖子
     */
    @PostMapping("/getMyPosting")
    public Result<List<Long>> getMyPosting() {
        return postingService.getMyPosting();
    }
}
