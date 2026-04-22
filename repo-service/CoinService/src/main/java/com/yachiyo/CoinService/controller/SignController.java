package com.yachiyo.CoinService.controller;

import com.yachiyo.CoinService.result.Result;
import com.yachiyo.CoinService.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/sign")
public class SignController {
    @Autowired
    private SignService signService;

    /**
     * 签到
     * @return 签到结果
     */
    @PostMapping("check-in")
    public Result<Boolean> signIn() {
        return signService.signIn();
    }

    /**
     * 获取签到状态
     * @return 当前签到状态
     */
    @PostMapping("/status")
    public Result<Boolean> getSignInStatus() {
        return signService.getSignInStatus();
    }
}
