package com.yachiyo.QQBotService.plugin;

import com.mikuac.shiro.annotation.GroupDecreaseHandler;
import com.mikuac.shiro.annotation.GroupIncreaseHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import com.yachiyo.QQBotService.service.ChatGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Shiro
@Component
@Slf4j
public class ChatGroupPlugin {
    @Autowired
    private ChatGroupService chatGroupService;

    @GroupIncreaseHandler
    public int onGroupIncrease(Bot bot, GroupIncreaseNoticeEvent event) {
        Long userId = event.getUserId();
        if (userId.equals(bot.getSelfId())) {
            chatGroupService.onGroupJoin(chatGroupService.computeChatGroup(event));
        } else {
            log.info("非本机器人入群，群号：{}，用户ID：{}，暂无操作", event.getGroupId(), userId);
        }
        return BotPlugin.MESSAGE_IGNORE;
    }

    @GroupDecreaseHandler
    public int onGroupDecrease(Bot bot, GroupDecreaseNoticeEvent event) {
        Long userId = event.getUserId();
        if (userId.equals(bot.getSelfId())) {
             chatGroupService.onGroupExist(event.getGroupId());
        } else {
            log.info("非本机器人退群，群号：{}，用户ID：{}，暂无操作", event.getGroupId(), userId);
        }
        return BotPlugin.MESSAGE_IGNORE;
    }
}
