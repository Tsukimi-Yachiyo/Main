package com.yachiyo.service.Impl;

import com.yachiyo.result.Result;
import com.yachiyo.service.ToolService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ToolServiceImpl implements ToolService {

    @Resource(name = "Live2dModel")
    private ChatClient chatClient;


    @Override
    public Result<String> getLive2dJson(String prompt) {

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return Result.success(response);
    }
}
