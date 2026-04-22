package com.yachiyo.PostingService.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data @TableName("posting_detail")
public class PostDetail {

    private Long id;

    private Long love;

    private Long collection;

    private Long reading;

    private Long coin;
}
