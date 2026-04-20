package com.yachiyo.QQBotService.plugin;

import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.GroupMsgDeleteNoticeHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupMsgDeleteNoticeEvent;
import com.yachiyo.QQBotService.result.Result;
import com.yachiyo.QQBotService.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Shiro
@Component
@Slf4j
// TODO: 解析合并转发消息：[CQ:forward,id=7630148198419583107]
// TODO: 生成回复消息的CQ码
public class GroupMessagePlugin {
    @Autowired
    private MessageService messageService;

    @GroupMessageHandler
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        Result<?> result = messageService.saveMessage(messageService.computeMessage(bot, event));
        log.info("保存群消息，消息id：{}，结果：{}", event.getMessageId() ,result);
        return BotPlugin.MESSAGE_IGNORE;
    }

    @GroupMsgDeleteNoticeHandler // 撤回消息
    public int onGroupMsgDeleteNotice(Bot bot, GroupMsgDeleteNoticeEvent event) {
        Result<?> result = messageService.deleteMessage(event.getMessageId());
        log.info("删除群消息，消息id：{}，结果：{}", event.getMessageId() ,result);
        return BotPlugin.MESSAGE_IGNORE;
    }
}
