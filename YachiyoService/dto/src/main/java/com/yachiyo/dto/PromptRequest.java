package com.yachiyo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor
@NoArgsConstructor
public class PromptRequest {

    private String user;

    private String assistant;
}
