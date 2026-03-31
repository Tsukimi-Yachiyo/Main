package com.yachiyo.controller;

import com.yachiyo.dto.CoinChangeRequest;
import com.yachiyo.enumeration.TradeType;
import com.yachiyo.result.Result;
import com.yachiyo.service.CoinChangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/coin")
@RequiredArgsConstructor
@Validated
public class CoinChangeController {

    private final CoinChangeService coinChangeService;

    /**
     * 金币交易
     * @param coinChangeRequest 金币交易请求
     * @return 金币交易结果
     */
    @PostMapping("/change")
    public Result<Boolean> changeCoin(@RequestBody @Valid CoinChangeRequest coinChangeRequest) {
        return coinChangeService.changeCoin(coinChangeRequest);
    }

    /**
     * 获取金币
     * @return 金币
     */
    @PostMapping("/get")
    public Result<Integer> getCoin() {
        return coinChangeService.getCoin();
    }
}
