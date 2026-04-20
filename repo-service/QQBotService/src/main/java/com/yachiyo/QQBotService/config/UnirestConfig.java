package com.yachiyo.QQBotService.config;

import jakarta.annotation.PostConstruct;
import kong.unirest.core.Unirest;
import kong.unirest.modules.gson.GsonObjectMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnirestConfig {
    @PostConstruct
    public void init() {
        // 使用 Gson 作为 Unirest 的 JSON 解析器
        Unirest.config().setObjectMapper(new GsonObjectMapper());
    }
}
