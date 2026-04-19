package com.yachiyo.CoinService.controller.internal;

import com.yachiyo.CoinService.dto.CoinChangeRequest;
import com.yachiyo.CoinService.result.Result;
import com.yachiyo.CoinService.service.CoinService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/coin")
public class CoinController {

    @Autowired
    private CoinService coinService;

    /**
     * 金币交易
     * @param coinChangeRequest 金币交易请求
     * @return 金币交易结果
     */
    @PostMapping("/change")
    public Result<Boolean> changeCoin(@RequestParam Long userId,
                                          @RequestBody CoinChangeRequest coinChangeRequest) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, null);

        SecurityContextHolder.getContext().setAuthentication(auth);
        return coinService.changeCoin(coinChangeRequest);
    }

    /**
     * 获取金币
     * @return 金币
     */
    @PostMapping("/get")
    public Result<Integer> getCoin(@RequestParam Long userId) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return coinService.getCoin();
    }
}
