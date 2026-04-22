package com.yachiyo.QQBotService.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_groups")
public class ChatGroup {
    @TableId
    private Long groupId; // 群号

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinTime; // 机器人入群时间
}
