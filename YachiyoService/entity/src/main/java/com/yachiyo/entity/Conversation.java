package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data @TableName("llm_conversations")
public class Conversation {

    @TableId("conversation_id")
    int id;

    int userId;

    String title;

    Date createTime;

    Date updateTime;
}
