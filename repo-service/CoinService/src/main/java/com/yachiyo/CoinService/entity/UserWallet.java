package com.yachiyo.CoinService.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("user_wallet")
public class UserWallet {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Double balance;

    @Version
    private Integer version;

    @TableField("create_time")
    private LocalDate createTime;

    @TableField("update_time")
    private LocalDate updateTime;
}
