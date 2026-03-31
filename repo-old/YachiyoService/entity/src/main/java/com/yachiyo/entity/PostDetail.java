package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data @TableName("posting_detail")
public class PostDetail {

    private int id;

    private int like;

    private int collection;

    private int read;
}
