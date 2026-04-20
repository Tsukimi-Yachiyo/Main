package com.yachiyo.QQBotService.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.yachiyo.QQBotService.dto.UploadFileRequest;
import com.yachiyo.QQBotService.entity.ChatGroup;
import com.yachiyo.QQBotService.entity.Message;
import com.yachiyo.QQBotService.mapper.ChatGroupMapper;
import com.yachiyo.QQBotService.mapper.MessageMapper;
import com.yachiyo.QQBotService.result.Result;
import com.yachiyo.QQBotService.service.FileService;
import com.yachiyo.QQBotService.service.MessageService;
import com.yachiyo.QQBotService.utils.CQCodeUtils;
import com.yachiyo.QQBotService.utils.UnixUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatGroupMapper chatGroupMapper;

    @Autowired
    private UnixUtils unixUtils;

    @Autowired
    private CQCodeUtils CQCodeUtils;

    @Autowired
    private FileService fileService;

    @Override
    public Result<Boolean> saveMessage(Message message) {
        try {
            if (chatGroupMapper.selectById(message.getGroupId()) == null) {
                ChatGroup chatGroup = new ChatGroup();
                chatGroup.setGroupId(message.getGroupId());
                chatGroupMapper.insert(chatGroup);
            }

            int rows = messageMapper.insert(message);
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
    public Result<Boolean> deleteMessage(Integer messageId) {
        try {
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_id", messageId);
            Message message = messageMapper.selectOne(queryWrapper);

            if (message == null) {
                return Result.error("404", "消息不存在", null);
            }

            message.setIsRecalled(true);

            int rows = messageMapper.updateById(message);
            return Result.success(rows > 0);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage(), null);
        }
    }

    @Override
    public Message computeMessage(Bot bot, GroupMessageEvent event) {
        Message message = new Message();

        message.setGroupId(event.getGroupId());
        message.setSendTime(unixUtils.ofSecond(event.getTime()));
        message.setMessageId(Long.valueOf(event.getMessageId()));
        message.setSenderId(event.getUserId());
        message.setPlainText(event.getPlainText());
        message.setRawMessage(event.getMessage());

        message.setBySelf(event.getUserId().equals(bot.getSelfId()));
        message.setIsRecalled(false);

        message.setAtList(CQCodeUtils.getAtIdList(event.getArrayMsg()));
        // 1. 解析CQ码中的可下载内容（如图片、语音、视频等），获取它们的URL和文件名
        List<UploadFileRequest> uploadFileList = CQCodeUtils.getUploadFileList(event.getArrayMsg());
        // 2. 下载这些内容并上传到MinIO，获取其新的文件名
        // 3. 将已保存的文件名列表保存到数据库
        message.setFileList(fileService.uploadFiles(uploadFileList).getData());

        return message;
    }
}
