package com.yachiyo.PostingService.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data @TableName("posting_coin")
public class LinkCoin {

    private Long userId;

    private Long postingId;
}
