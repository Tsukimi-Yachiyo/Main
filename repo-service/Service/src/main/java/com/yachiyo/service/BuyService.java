package com.yachiyo.service;

import com.yachiyo.dto.BuyResponse;
import com.yachiyo.entity.Good;
import com.yachiyo.result.Result;

import java.util.List;

public interface BuyService {

    /**
     * 购买
     */
    Result<Boolean> buy(Integer goodId);

    /**
     * 获取购买列表
     */
    Result<List<BuyResponse>> getBuyList();

    /**
     * 获取所有礼物
     */
    Result<List<Good>> getAllGoods();

}
