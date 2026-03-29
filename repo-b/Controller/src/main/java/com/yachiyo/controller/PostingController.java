package com.yachiyo.controller;

import com.yachiyo.dto.GetPostingResponse;
import com.yachiyo.dto.InteractionRequest;
import com.yachiyo.dto.PostStatsResponse;
import com.yachiyo.dto.SelfPostResponse;
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
     * 删除帖子
     */
    @PostMapping("/delete")
    public Result<Boolean> deletePosting(@RequestParam Long postingId) {
        return postingService.deletePosting(postingId);
    }

    /**
     * 处理帖子互动（点赞/收藏）
     */
    @PostMapping("/interaction")
    public Result<Boolean> handleInteraction(@RequestBody InteractionRequest request) {
        return postingService.handleInteraction(request);
    }

    /**
     * 获取帖子统计信息
     */
    @PostMapping("/stats")
    public Result<PostStatsResponse> getPostingStats(@RequestParam Long postingId) {
        return postingService.getPostingStats(postingId);
    }

    /**
     * 获取自己的帖子
     */
    @PostMapping("/getMyPosting")
    public Result<List<SelfPostResponse>> getMyPosting() {
        return postingService.getMyPosting();
    }
}
