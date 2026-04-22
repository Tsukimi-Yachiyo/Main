package com.yachiyo.CoinService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.CoinService.dto.CoinChangeRequest;
import com.yachiyo.CoinService.entity.CoinLog;
import com.yachiyo.CoinService.mapper.CoinLogMapper;
import com.yachiyo.CoinService.result.Result;
import com.yachiyo.CoinService.service.CoinService;
import com.yachiyo.CoinService.service.SignService;
import com.yachiyo.CoinService.utils.CurrentUserIdProvider;
import com.yachiyo.CoinService.utils.TradeType;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SignServiceImpl implements SignService {
    @Autowired
    private CoinLogMapper coinLogMapper;

    @Autowired
    private CoinService coinService;

    @Autowired
    private CurrentUserIdProvider currentUserIdProvider;

    @Override
    public Result<Boolean> signIn() {
        // 从安全上下文获取当前用户id
        Long userId = currentUserIdProvider.getCurrentUserId();

        // 从数据库中获取用户详情
//        UserDetail userDetail = userDetailMapper.selectById(userId);
//        if (userDetail == null) {
//            return Result.error("404", "用户不存在"+userId);
//        }

        Long signCount = getSignCountForToday(userId);
        if (signCount == null) {
            return Result.error("500", "查询签到状态失败", null);
        }
        if (signCount > 0) {
            return Result.error("400", "今日已签到", null);
        }

        // 签到成功
        CoinChangeRequest coinChangeRequest = new CoinChangeRequest();
        coinChangeRequest.setToUserId(userId);
        coinChangeRequest.setType(TradeType.CHECKIN);
        coinChangeRequest.setAmount(8.0);
        return coinService.changeCoin(coinChangeRequest);
    }

    @Override
    public Result<Boolean> getSignInStatus() {
        Long signCount = getSignCountForToday(currentUserIdProvider.getCurrentUserId());

        if (signCount == null) {
            return Result.error("500", "查询签到状态失败", null);
        }

        return Result.success(signCount > 0);
    }

    @Nullable
    private Long getSignCountForToday(Long userId) {
        try {
            QueryWrapper<CoinLog> qw = new QueryWrapper<>();
            qw.eq("user_id", userId)
                    .eq("business_type", TradeType.CHECKIN.name())
                    .eq("create_time", LocalDate.now());
            return coinLogMapper.selectCount(qw);
        } catch (Exception e) {
            return null;
        }
    }
}
