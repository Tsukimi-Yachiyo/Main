package com.yachiyo.CoinService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yachiyo.CoinService.entity.UserWallet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserWalletMapper extends BaseMapper<UserWallet> {
    /**
     * 返回1表示成功插入，返回0表示已经存在（冲突）并且DO NOTHING
     * @param id 用户ID
     * @return 插入结果
     */
    @Insert("INSERT INTO user_wallet (id) VALUES (#{id}) ON CONFLICT (id) DO NOTHING")
    int initWallet(Long id);
}
