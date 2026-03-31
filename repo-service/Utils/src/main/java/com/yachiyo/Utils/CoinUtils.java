package com.yachiyo.Utils;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yachiyo.entity.CoinLog;
import com.yachiyo.entity.UserWallet;
import com.yachiyo.mapper.CoinLogMapper;
import com.yachiyo.mapper.UserWalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        userWallet.setBalance(userWallet.getBalance() + amount);
        userWallet.setVersion(userWallet.getVersion() + 1);

        // 保存用户信息
        if (userWalletMapper.update(userWallet, new UpdateWrapper<UserWallet>().eq("version", userWallet.getVersion()).eq("id", toUserId)) == 0) {
            throw new IllegalArgumentException("更新用户余额失败");
        }

        // 记录交易日志
        coinLogMapper.insert(new CoinLog(null, toUserId, amount, userWallet.getBalance(), userWallet.getBalance() + amount, businessType, LocalDateTime.now()));
    }

    public Integer getCoin(Long userId) {
        UserWallet userWallet = userWalletMapper.selectById(userId);
        if (userWallet.getBalance() < 0) {
            return null;
        }
        return userWallet.getBalance().intValue();
    }
}
