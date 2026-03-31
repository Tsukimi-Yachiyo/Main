package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data @TableName("posting_like")
public class LinkLike {

    private Long userId;

    private Long postingId;
}
