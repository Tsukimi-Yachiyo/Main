package com.yachiyo.service;

import com.yachiyo.dto.CoinChangeRequest;
import com.yachiyo.result.Result;

public interface CoinChangeService {

    /**
     * 修改金币
     * @param request 修改金币请求
     * @return 修改结果
     */
    Result<Boolean> changeCoin(CoinChangeRequest request);

    /**
     * 获取金币
     * @return 金币
     */
    Result<Integer> getCoin();

}
