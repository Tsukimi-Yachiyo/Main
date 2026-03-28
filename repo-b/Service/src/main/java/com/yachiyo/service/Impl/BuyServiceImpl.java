package com.yachiyo.service.Impl;

import com.yachiyo.dto.BuyResponse;
import com.yachiyo.dto.CoinChangeRequest;
import com.yachiyo.entity.Good;
import com.yachiyo.entity.User;
import com.yachiyo.enumeration.TradeType;
import com.yachiyo.mapper.GoodMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.BuyService;
import com.yachiyo.service.CoinChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BuyServiceImpl implements BuyService {

    @Autowired
    private CoinChangeService coinChangeService;

    @Autowired
    private GoodMapper goodMapper;

    @Override
    public Result<Boolean> buy(Integer goodId) {
        Long userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        CoinChangeRequest coinChangeRequest = new CoinChangeRequest();
        Good good = goodMapper.selectById(goodId);
        coinChangeRequest.setFromUserId(null);
        coinChangeRequest.setToUserId(userId);
        coinChangeRequest.setType(TradeType.BUY);
        coinChangeRequest.setAmount(-good.getPrice());
        return coinChangeService.changeCoin(coinChangeRequest);
    }

    @Override
    public Result<List<BuyResponse>> getBuyList() {
        return null;
    }

    @Override
    public Result<List<Good>> getAllGoods() {
        return null;
    }
}
