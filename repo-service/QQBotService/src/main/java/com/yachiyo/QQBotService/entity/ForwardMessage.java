package com.yachiyo.QQBotService.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import lombok.Data;

import java.util.List;

@Data
@TableName("forward_messages")
public class ForwardMessage {
    @TableField("forward_id")
    private Long forwardId;

    @TableField(value = "message_list", typeHandler = GsonTypeHandler.class)
    private List<String> messages;

    @TableField("has_child")
    private Boolean hasChild;
}
