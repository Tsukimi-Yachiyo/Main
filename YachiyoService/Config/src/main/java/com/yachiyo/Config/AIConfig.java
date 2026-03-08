package com.yachiyo.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openaisdk.OpenAiSdkChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AIConfig {

    final OllamaChatModel model;

    final OpenAiSdkChatModel openAiSdkChatModel;

    @Value("${live2D.system}")
    private String SystemPrompt;

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder().build();
    }

    @Bean("ChatModel")
    public ChatClient chatClient() {
        return ChatClient.builder(model)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory()).build()
                )
                .build();
    }

    @Bean("Live2dModel")
    public ChatClient openAiChatClient() {
        return ChatClient.builder(openAiSdkChatModel)
                .defaultSystem(SystemPrompt)
                .build();
    }
}