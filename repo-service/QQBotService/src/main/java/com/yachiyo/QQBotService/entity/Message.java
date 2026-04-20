package com.yachiyo.QQBotService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id; // 自增主键

    @TableField("send_time")
    private LocalDateTime sendTime; // 发送时间

    @TableField("group_id")
    private Long groupId; // 群号

    @TableField("message_id")
    private Long messageId; // NapCat 消息ID

    @TableField("sender_id")
    private Long senderId; // 发送者QQ号

    @TableField("plain_text")
    private String plainText; // 纯文本内容（不含CQ码）

    @TableField("raw_message")
    private String rawMessage; // 原始消息（含CQ码）

    @TableField("by_self")
    private Boolean bySelf; // 是否机器人自己发送的

    @TableField("is_recalled")
    private Boolean isRecalled; // 是否已被撤回

    @TableField(value = "cq_at_list", typeHandler = GsonTypeHandler.class)
    private List<Long> atList; // at列表（JSON字符串）

    @TableField(value = "cq_file_list", typeHandler = GsonTypeHandler.class)
    private List<String> fileList; // minio的url列表（JSON字符串）
}
