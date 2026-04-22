package com.yachiyo.CoinService.service;

import com.yachiyo.CoinService.dto.CoinChangeRequest;
import com.yachiyo.CoinService.result.Result;

public interface CoinService {
    Result<Boolean> changeCoin(CoinChangeRequest coinChangeRequest);

    Result<Integer> getCoin();
}
