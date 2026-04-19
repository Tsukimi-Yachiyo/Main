package com.yachiyo.CoinService.service;

import com.yachiyo.CoinService.result.Result;

public interface SignService {
    Result<Boolean> signIn();

    Result<Boolean> getSignInStatus();
}
