package com.yachiyo.QQBotService.plugin;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.GroupMsgDeleteNoticeHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupMsgDeleteNoticeEvent;
import com.yachiyo.QQBotService.service.GroupMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Shiro
@Component
@Slf4j
// TODO: 生成回复消息的CQ码
public class GroupMessagePlugin {
    @Autowired
    private GroupMessageService groupMessageService;

    @GroupMessageHandler
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        groupMessageService.onSendMessage(groupMessageService.computeGroupMessage(bot, event));
        return BotPlugin.MESSAGE_IGNORE;
    }

    @GroupMsgDeleteNoticeHandler // 撤回消息
    public int onGroupMsgDeleteNotice(Bot bot, GroupMsgDeleteNoticeEvent event) {
        groupMessageService.onDeleteMessage(event.getMessageId());
        return BotPlugin.MESSAGE_IGNORE;
    }
}
