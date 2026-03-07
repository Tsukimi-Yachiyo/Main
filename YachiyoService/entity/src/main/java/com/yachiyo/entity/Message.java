package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data @TableName("llm_conversation_messages")
public class Message {

    @TableId("message_id")
    int id;

    int conversationId;

    String user;

    String assistant;

    Date time;
}
