package com.yachiyo.CoinService.utils;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yachiyo.CoinService.entity.CoinLog;
import com.yachiyo.CoinService.entity.UserWallet;
import com.yachiyo.CoinService.mapper.CoinLogMapper;
import com.yachiyo.CoinService.mapper.UserWalletMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class CoinUtils {
    @Autowired
    private CoinLogMapper coinLogMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    public void changeCoin(Long toUserId, Double amount, String businessType) {
        // 从数据库中查询用户信息
        UserWallet userWallet = userWalletMapper.selectById(toUserId);

        // 检查用户是否存在
        if (userWallet == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 检查用户是否有足够的余额
        if (userWallet.getBalance() < -amount) {
            throw new IllegalArgumentException("余额不足");
        }

        // 更新用户余额
        Double beforeBalance = userWallet.getBalance();
        userWallet.setBalance(userWallet.getBalance() + amount);

        // 保存用户信息
        if (userWalletMapper.update(userWallet, new UpdateWrapper<UserWallet>().eq("id", toUserId)) == 0) {
            log.info("待更新ID：{}，版本号：{}", toUserId, userWallet.getVersion());
            throw new IllegalArgumentException("更新用户余额失败");
        }

        // 记录交易日志
        coinLogMapper.insert(new CoinLog(
                null, toUserId, amount,
                beforeBalance,
                userWallet.getBalance(),
                businessType, LocalDate.now()
        ));
    }

    public Integer getCoin(Long userId) {
        UserWallet userWallet = userWalletMapper.selectById(userId);
        if (userWallet.getBalance() < 0) {
            // 余额为负数，返回null表示异常情况
            return null;
        }
        return userWallet.getBalance().intValue();
    }
}
