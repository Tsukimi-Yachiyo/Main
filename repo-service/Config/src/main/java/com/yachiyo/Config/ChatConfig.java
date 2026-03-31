package com.yachiyo.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "chat")
@Data
public class ChatConfig {
    private List<String> userMessage;
    private List<String> systemMessage;
}
