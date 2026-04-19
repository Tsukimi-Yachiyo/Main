package com.yachiyo.PostingService.client;

import com.yachiyo.PostingService.dto.CoinChangeRequest;
import com.yachiyo.PostingService.result.Result;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coin-service", path = "/internal/coin")
public interface CoinClient {

    @PostMapping("/change")
    public Result<Boolean> changeCoin(
            @RequestParam Long userId,
            @RequestBody @Valid CoinChangeRequest coinChangeRequest);

}
