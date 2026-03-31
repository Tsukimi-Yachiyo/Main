package com.yachiyo.dto;

import lombok.Data;

@Data
public class CommentRequest {

    private Long postingId;
    private String content;
}
