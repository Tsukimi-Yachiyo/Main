package com.yachiyo.QQBotService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.response.MsgResp;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yachiyo.QQBotService.dto.UploadFileRequest;
import com.yachiyo.QQBotService.entity.ChatGroup;
import com.yachiyo.QQBotService.entity.ForwardMessage;
import com.yachiyo.QQBotService.entity.GroupMessage;
import com.yachiyo.QQBotService.mapper.ChatGroupMapper;
import com.yachiyo.QQBotService.mapper.ForwardMessageMapper;
import com.yachiyo.QQBotService.mapper.MessageMapper;
import com.yachiyo.QQBotService.result.Result;
import com.yachiyo.QQBotService.service.FileService;
import com.yachiyo.QQBotService.service.GroupMessageService;
import com.yachiyo.QQBotService.utils.CQCodeUtils;
import com.yachiyo.QQBotService.utils.MessageUtils;
import com.yachiyo.QQBotService.utils.UnixUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ForwardMessageMapper forwardMessageMapper;

    @Autowired
    private ChatGroupMapper chatGroupMapper;

    @Autowired
    private UnixUtils unixUtils;

    @Autowired
    private CQCodeUtils CQCodeUtils;

    @Autowired
    private FileService fileService;

    @Autowired
    private MessageUtils messageUtils;

    @Override
    public Result<Boolean> onSendMessage(GroupMessage groupMessage) {
        try {
            if (chatGroupMapper.selectById(groupMessage.getGroupId()) == null) {
                ChatGroup chatGroup = new ChatGroup();
                chatGroup.setGroupId(groupMessage.getGroupId());
                chatGroupMapper.insert(chatGroup);
            }

            int rows = messageMapper.insert(groupMessage);
            if (rows > 0) {
                return Result.success(true);
            } else {
                return Result.error("保存消息失败", null);
            }
        } catch (Exception e) {
            return Result.error("保存消息失败 : ", e.getMessage());
        }
    }

    @Override
    public Result<Boolean> onDeleteMessage(Integer messageId) {
        try {
            QueryWrapper<GroupMessage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_id", messageId);
            GroupMessage groupMessage = messageMapper.selectOne(queryWrapper);

            if (groupMessage == null) {
                return Result.error("404", "消息不存在", null);
            }

            groupMessage.setIsRecalled(true);

            int rows = messageMapper.updateById(groupMessage);
            return Result.success(rows > 0);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage(), null);
        }
    }

    @Override
    public GroupMessage computeGroupMessage(Bot bot, GroupMessageEvent event) {
        GroupMessage groupMessage = new GroupMessage();

        groupMessage.setGroupId(event.getGroupId());
        groupMessage.setSendTime(unixUtils.ofSecond(event.getTime()));
        groupMessage.setMessageId(Long.valueOf(event.getMessageId()));
        groupMessage.setSenderId(event.getUserId());
        groupMessage.setPlainText(event.getPlainText());

        groupMessage.setBySelf(event.getUserId().equals(bot.getSelfId()));
        groupMessage.setIsRecalled(false);

        groupMessage.setAtList(CQCodeUtils.getAtIdList(event.getArrayMsg()));
        groupMessage.setFileList(uploadFilesForName(event));

        var formattedMessage = messageUtils.format(event);
        groupMessage.setMessageForAgent(formattedMessage.getContent());
        groupMessage.setRelevantUrls(formattedMessage.getRelevantUrls());

        saveForwardMessage(bot, event);

        return groupMessage;
    }

    /**
     * 解析消息中的CQ码，下载其中包含的文件（如图片、语音、视频等），上传到MinIO，并返回新的文件名列表
     * @param event 消息事件
     * @return 上传后的文件名列表，若消息中不包含可下载的CQ码或上传失败则返回空列表
     */
    private List<String> uploadFilesForName(GroupMessageEvent event) {
        // 1. 解析CQ码中的可下载内容（如图片、语音、视频等），获取它们的URL和文件名
        List<UploadFileRequest> uploadFileList = CQCodeUtils.getUploadFileList(event.getArrayMsg());
        // 2. 下载这些内容并上传到MinIO，获取其新的文件名
        // 3. 将已保存的文件名列表保存到数据库
        return fileService.uploadFiles(uploadFileList).getData();
    }

    /**
     * 如果消息中包含合并转发消息，则将其内容保存到数据库中，不支持嵌套的合并转发消息，因为ShiroSDK返回的DTO中无法查询到messageId
     * @param bot 用于调用API获取合并转发消息内容
     * @param event 消息事件
     */
    private void saveForwardMessage(Bot bot, GroupMessageEvent event) {
        long forwardId = CQCodeUtils.findForwardId(event.getArrayMsg());
        if (forwardId < 0) {
            return;
        }

        // 避免重复保存
        QueryWrapper<ForwardMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forward_id", forwardId);
        if (forwardMessageMapper.exists(queryWrapper)) {
            return;
        }

        boolean hasChild = false;
        List<String> messageList = new ArrayList<>();

        var forwardMsgResp = bot.getForwardMsg(event.getMessageId());
        if (forwardMsgResp == null || forwardMsgResp.getData() == null) {
            return;
        }
        for (MsgResp msgResp : forwardMsgResp.getData().getMessages()) {
            if (CQCodeUtils.containsForward(msgResp)) {
                hasChild = true;
            }
            messageList.add(messageUtils.format(msgResp).getContent());
        }

        var forwardMessage = new ForwardMessage();

        forwardMessage.setForwardId(forwardId);
        forwardMessage.setMessages(messageList);
        forwardMessage.setHasChild(hasChild);

        forwardMessageMapper.insert(forwardMessage);
    }
}
