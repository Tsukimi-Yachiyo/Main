package com.yachiyo.QQBotService.service;

import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import com.yachiyo.QQBotService.entity.ChatGroup;
import com.yachiyo.QQBotService.result.Result;

public interface ChatGroupService {
    Result<Boolean> onGroupJoin(ChatGroup chatGroup);

    Result<Boolean> onGroupExist(Long groupId);

    ChatGroup computeChatGroup(GroupIncreaseNoticeEvent event);
}
