package com.yachiyo.Config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.dto.PromptRequest;
import com.yachiyo.entity.Conversation;
import com.yachiyo.entity.Message;
import com.yachiyo.entity.User;
import com.yachiyo.mapper.ConversationMapper;
import com.yachiyo.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatMemoryHistoryToolConfig {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 保存对话
     * @param id 会话id
     * @param UserContent 用户内容
     * @param YachiyoContent 机器人内容
     */
    public void save(int id,String UserContent,String YachiyoContent) throws Exception {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation != null) {
            conversationMapper.updateById(conversation);
            Message message = new Message();
            message.setId(id);
            message.setConversationId(id);
            message.setUser(UserContent);
            message.setAssistant(YachiyoContent);
            if (messageMapper.insert(message) > 0) {
                log.info("保存对话成功");
            }else {
                throw new Exception("保存对话失败");
            }
        }else {
            throw new Exception("会话不存在");
        }
    }

    /**
     * 创建对话
     * @return 是否创建成功
     */
    public int create() throws Exception {
        int id = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        Conversation conversation = new Conversation();
        conversation.setUserId(id);
        if (conversationMapper.insert(conversation) > 0) {
            return conversation.getId();
        }else {
            throw new Exception("创建会话失败");
        }
    }

     /**
     * 获取对话记忆
     * @param id 会话id
     * @return 对话记忆
     */
    public List<PromptRequest> getHistory(int id) throws Exception {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation != null) {
            List<Message> messages = messageMapper.selectList(new QueryWrapper<Message>().eq("conversation_id", id));
            if (messages != null) {
                return messages.stream().map(message -> new PromptRequest(message.getUser(), message.getAssistant())).collect(Collectors.toList());
            }else {
                throw new Exception("获取对话记忆失败");
            }
        }else {
            throw new Exception("会话不存在");
        }
    }

    /**
     * 获取会话列表
     * @return 会话列表
     */
    public List<Integer> getConservationIds() {
        User user = (User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
        return conversationMapper.selectList(new QueryWrapper<Conversation>().eq("user_id", user.getId())).stream().map(Conversation::getId).collect(Collectors.toList());
    }

    @Bean
    public ChatMemoryHistoryToolConfig getChatMemoryHistoryToolConfig(){
        return new ChatMemoryHistoryToolConfig();
    }
}

