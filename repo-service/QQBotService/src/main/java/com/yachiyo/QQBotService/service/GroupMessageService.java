package com.yachiyo.QQBotService.service;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yachiyo.QQBotService.entity.GroupMessage;
import com.yachiyo.QQBotService.result.Result;

public interface GroupMessageService {
    Result<Boolean> onSendMessage(GroupMessage groupMessage);

    Result<Boolean> onDeleteMessage(Integer messageId);

    GroupMessage computeGroupMessage(Bot bot, GroupMessageEvent event);
}
