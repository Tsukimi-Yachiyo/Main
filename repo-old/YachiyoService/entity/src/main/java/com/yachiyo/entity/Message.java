package com.yachiyo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.ai.chat.messages.MessageType;

import java.util.Date;

@Data @TableName("spring_ai_chat_memory")
public class Message {

    @TableId("conversation_id")
    int id;

    String content;

    MessageType type;

    @TableField("timestamp")
    Date time;
}
