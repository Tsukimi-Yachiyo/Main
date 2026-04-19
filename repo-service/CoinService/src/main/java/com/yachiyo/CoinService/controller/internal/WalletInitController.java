package com.yachiyo.CoinService.controller.internal;

import com.yachiyo.CoinService.mapper.UserWalletMapper;
import com.yachiyo.CoinService.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/wallet")
public class WalletInitController {
    @Autowired
    private UserWalletMapper userWalletMapper;

    @GetMapping("/init/{id}")
    public Result<Boolean> initWallet(@PathVariable Long id) {
        int result = userWalletMapper.initWallet(id);
        return Result.success(result > 0);
    }
}
