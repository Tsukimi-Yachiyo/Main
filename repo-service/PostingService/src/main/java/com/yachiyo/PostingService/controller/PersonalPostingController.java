package com.yachiyo.PostingService.controller;

import com.yachiyo.PostingService.dto.SelfPostResponse;
import com.yachiyo.PostingService.dto.UploadPostingRequest;
import com.yachiyo.PostingService.result.Result;
import com.yachiyo.PostingService.service.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v2/posting")
@RequiredArgsConstructor
@Validated
public class PersonalPostingController {
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
    public Result<List<SelfPostResponse>> getMyPosting() {
        return postingService.getMyPosting();
    }

}
