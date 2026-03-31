package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data @TableName("llm_conversations")
public class Conversation {

    @TableId(value = "conversation_id",type = IdType.AUTO)
    Long id;

    Long userId;

    String title;

    Date createTime;

    Date updateTime;
}
