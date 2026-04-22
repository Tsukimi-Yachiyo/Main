package com.yachiyo.AdminService.client;

import com.yachiyo.AdminService.dto.PostingQueryRequest;
import com.yachiyo.AdminService.dto.PostingResponse;
import com.yachiyo.AdminService.dto.ReviewRequest;
import com.yachiyo.AdminService.result.Result;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "posting-service", path = "/internal/posting")
public interface PostingClient {

    @PostMapping("/review")
    Result<Boolean> reviewPosting(@RequestBody @Valid ReviewRequest request);

    @PostMapping("/query-postings")
    Result<List<PostingResponse>> queryPostings(@RequestBody @Valid PostingQueryRequest request);
}
