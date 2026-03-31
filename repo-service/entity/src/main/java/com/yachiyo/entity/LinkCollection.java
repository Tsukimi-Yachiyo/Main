package com.yachiyo.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data @TableName("posting_collection")
public class LinkCollection {

    private Long userId;

    private Long postingId;
}
