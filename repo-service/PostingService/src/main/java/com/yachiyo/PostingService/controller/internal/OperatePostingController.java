package com.yachiyo.PostingService.controller.internal;

import com.yachiyo.PostingService.dto.PostingQueryRequest;
import com.yachiyo.PostingService.dto.ReviewRequest;
import com.yachiyo.PostingService.entity.Posting;
import com.yachiyo.PostingService.result.Result;
import com.yachiyo.PostingService.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/posting")
@RequiredArgsConstructor
@Validated
public class OperatePostingController {

    private final AdminService adminService;

    @PostMapping("/review")
    public Result<Boolean> reviewPosting(@RequestBody @Valid ReviewRequest request) {
        return adminService.reviewPosting(request);
    }

    @PostMapping("/query-postings")
    public Result<List<Posting>> queryPostings(@RequestBody @Valid PostingQueryRequest request) {
        return adminService.queryPostings(request);
    }
}
