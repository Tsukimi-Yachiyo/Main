package com.yachiyo.Config;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openaisdk.OpenAiSdkChatModel;
import org.springframework.ai.openaisdk.OpenAiSdkEmbeddingModel;
import org.springframework.ai.openaisdk.OpenAiSdkEmbeddingOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
public class AIConfig {

//    @Primary  // 核心注解：标记为优先 Bean
//    @Bean
//    public EmbeddingModel primaryEmbeddingModel(OllamaEmbeddingModel ollamaEmbeddingModel) {
//        return ollamaEmbeddingModel;
//    }

    final OpenAiSdkChatModel openAiSdkChatModel;

//    final OllamaChatModel ollamaChatModel;

    @Value("${live2D.system}")
    private String SystemPrompt;

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository){
        return  MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(3)
                .build();
    }



//    @Bean("ChatModel")
//    public ChatClient chatClient(ChatMemory chatMemory, Advisor retrievalAugmentationAdvisor) {
//        return ChatClient.builder(ollamaChatModel)
//                .defaultAdvisors(
//                        new SimpleLoggerAdvisor(),
//                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
//                        retrievalAugmentationAdvisor
//                )
//                .build();
//    }

    @Bean("ChatModel")
    public ChatClient chatClient(ChatMemory chatMemory, Advisor retrievalAugmentationAdvisor) {

        return ChatClient.builder(openAiSdkChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        retrievalAugmentationAdvisor
                )
                .build();
    }

    @Bean("Live2dModel")
    public ChatClient openAiChatClient() {
        return ChatClient.builder(openAiSdkChatModel)
                .defaultSystem(SystemPrompt)
                .build();
    }


    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return TokenTextSplitter.builder()
                .withChunkSize(1000)
                .withMinChunkSizeChars(100)
                .withKeepSeparator(true)
                .build();
    }

    @Bean
    public Advisor ragResourceService(VectorStore vectorStoreDocument) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.5)
                        .topK(3)
                        .vectorStore(vectorStoreDocument)
                        .build()).build();
    }
}

