package com.yachiyo.QQBotService.service.impl;

import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import com.yachiyo.QQBotService.entity.ChatGroup;
import com.yachiyo.QQBotService.mapper.ChatGroupMapper;
import com.yachiyo.QQBotService.result.Result;
import com.yachiyo.QQBotService.service.ChatGroupService;
import com.yachiyo.QQBotService.utils.UnixUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatGroupServiceImpl implements ChatGroupService {
    @Autowired
    private ChatGroupMapper chatGroupMapper;

    @Autowired
    private UnixUtils unixUtils;

    @Override
    public Result<Boolean> onGroupJoin(ChatGroup chatGroup) {
        try {
            int rows = chatGroupMapper.insert(chatGroup);
            if (rows > 0) {
                return Result.success(true);
            } else {
                return Result.error("保存群信息失败", null);
            }
        } catch (Exception e) {
            return Result.error("保存群信息失败", e.getMessage());
        }
    }

    @Override
    public Result<Boolean> onGroupExist(Long groupId) {
        try {
            int rows = chatGroupMapper.deleteById(groupId);
            if (rows > 0) {
                return Result.success(true);
            } else {
                return Result.error("删除群信息失败", null);
            }
        } catch (Exception e) {
            return Result.error("删除群信息失败 : ", e.getMessage());
        }
    }

    @Override
    public ChatGroup computeChatGroup(GroupIncreaseNoticeEvent event) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setGroupId(event.getGroupId());
        chatGroup.setJoinTime(unixUtils.ofSecond(event.getTime()));
        return chatGroup;
    }
}
