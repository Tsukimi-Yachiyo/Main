package com.yachiyo.AdminService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostingResponse {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private String type;

    private Boolean isApproved;

    private LocalDateTime createTime;

    private Long score;
}
