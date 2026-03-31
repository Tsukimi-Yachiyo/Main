package com.yachiyo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
public class PromptResponse {

    private String user;

    private String assistant;
}
