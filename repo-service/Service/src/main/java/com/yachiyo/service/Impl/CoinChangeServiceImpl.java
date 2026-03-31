package com.yachiyo.service.Impl;

import com.yachiyo.Utils.CoinUtils;
import com.yachiyo.dto.CoinChangeRequest;
import com.yachiyo.entity.User;
import com.yachiyo.enumeration.TradeType;
import com.yachiyo.result.Result;
import com.yachiyo.service.CoinChangeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CoinChangeServiceImpl implements CoinChangeService {

    @Autowired
    private CoinUtils coinUtils;

    @Override
    public Result<Boolean> changeCoin(CoinChangeRequest request) {
        try {
            String businessType = request.getType().name();
            Double amount = request.getAmount();
            Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
            if (request.getType() == TradeType.TIP) {
                if (UserId.equals(request.getToUserId()) || !UserId.equals(request.getFromUserId())) {
                    return Result.error("400", businessType, "打赏交易只能对其他用户进行操作");
                }else {
                    amount = amount > 0 ? amount : 0;
                    coinUtils.changeCoin(request.getFromUserId(), -amount, businessType);
                }
            }
            coinUtils.changeCoin(request.getToUserId(), amount, businessType);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("400", "更改用户余额失败", e.getMessage());
        }
    }

    @Override
    public Result<Integer> getCoin() {
        Long UserId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        return Result.success(coinUtils.getCoin(UserId));
    }
}
