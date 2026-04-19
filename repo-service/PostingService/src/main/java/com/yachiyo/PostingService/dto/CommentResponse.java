package com.yachiyo.PostingService.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
public class CommentResponse {

    private Long id;

    private Long userId;

    private String content;
}
