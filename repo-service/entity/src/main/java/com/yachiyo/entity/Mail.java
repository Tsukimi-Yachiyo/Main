package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data @TableName("mail")
public class Mail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    @TableField("receiver_id")
    private Long receiverId;

    @TableField("sender_id")
    private Long senderId;

    @TableField("is_special")
    private Boolean isSpecial;

    @TableField("is_read")
    private Boolean isRead;
}
