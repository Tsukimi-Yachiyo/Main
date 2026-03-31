package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @TableName("coin_log") @AllArgsConstructor
@NoArgsConstructor
public class CoinLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("change_amount")
    private Double changeAmount;

    @TableField("before_balance")
    private Double beforeBalance;

    @TableField("after_balance")
    private Double afterBalance;

    @TableField("business_type")
    private String businessType;

    @TableField("create_time")
    private LocalDateTime createTime;

}
