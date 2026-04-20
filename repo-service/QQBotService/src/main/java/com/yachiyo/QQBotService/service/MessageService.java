package com.yachiyo.QQBotService.service;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yachiyo.QQBotService.entity.Message;
import com.yachiyo.QQBotService.result.Result;

public interface MessageService {
    Result<Boolean> saveMessage(Message message);

    Result<Boolean> deleteMessage(Integer messageId);

    Message computeMessage(Bot bot, GroupMessageEvent event);
}
