package com.yachiyo.Config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yachiyo.dto.ConversationResponse;
import com.yachiyo.dto.PromptResponse;
import com.yachiyo.entity.Conversation;
import com.yachiyo.entity.Message;
import com.yachiyo.entity.User;
import com.yachiyo.mapper.ConversationMapper;
import com.yachiyo.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
     * @param prompt 问题
     */
    public void save(int id,String prompt) throws Exception {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw new Exception("会话不存在");
        }
        Message message = new Message();
        message.setId(id);
        if (messageMapper.selectList(new QueryWrapper<Message>().eq("conversation_id",conversation.getId())).isEmpty()) {
            conversation.setTitle(prompt);
            conversationMapper.updateById(conversation);
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
    public List<PromptResponse> getHistory(int id) throws Exception {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation != null) {
            List<Message> messages = messageMapper.selectList(new QueryWrapper<Message>().eq("conversation_id", id));
            if (messages != null) {
                List<String> user = new ArrayList<>();
                List<String> yachiyo = new ArrayList<>();
                for (Message message : messages) {
                    if (message.getType() == MessageType.USER) {
                        user.add(message.getContent());}
                    else if (message.getType() == MessageType.ASSISTANT) {
                        yachiyo.add(message.getContent());}
                }
                return user.stream().map(u -> new PromptResponse(u, yachiyo.get(user.indexOf(u)))).collect(Collectors.toList());
            }else {
                throw new Exception("获取对话记忆失败");
            }
        }else {
            throw new Exception("会话不存在");
        }
    }

     /**
     * 清空对话记忆
     * @param id 会话id
     */
    public void clearHistory(int id) throws Exception {
        if (conversationMapper.selectById(id) == null) {
            throw new Exception("会话不存在");
        }else {
            if (conversationMapper.deleteById(id) > 0) {
                log.info("删除会话成功");
            }else {
                throw new Exception("删除会话失败");
            }

        }
        if (messageMapper.delete(new QueryWrapper<Message>().eq("conversation_id", id)) > 0) {
            log.info("删除会话记忆成功");
        }else {
            throw new Exception("删除会话记忆失败");
        }
    }

    /**
     * 清空所有对话记忆
     */
    public void clearAllHistory() throws Exception {
        int userId = ((User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal())).getId();
        conversationMapper.deleteById(userId);
        if (messageMapper.delete(new QueryWrapper<Message>().eq("user_id", userId)) > 0) {
            log.info("清空所有会话记忆成功");
        }else {
            throw new Exception("清空所有会话记忆失败");
        }
    }

    /**
     * 获取会话列表
     * @return 会话列表
     */
    public List<ConversationResponse> getConservationIds() {
        User user = (User) Objects.requireNonNull(Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal());
        return conversationMapper.selectList(new QueryWrapper<Conversation>().eq("user_id", user.getId())).stream().map(conversation -> new ConversationResponse(conversation.getId(), conversation.getTitle())).collect(Collectors.toList());
    }

    @Bean
    public ChatMemoryHistoryToolConfig getChatMemoryHistoryToolConfig(){
        return new ChatMemoryHistoryToolConfig();
    }

    public void changeTitle(int conversationId, String title) throws Exception {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new Exception("会话不存在");
        }
        conversation.setTitle(title);
        conversationMapper.updateById(conversation);
    }
}

