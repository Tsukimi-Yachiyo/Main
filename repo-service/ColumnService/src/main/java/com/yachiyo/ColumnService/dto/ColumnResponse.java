package com.yachiyo.ColumnService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor
@NoArgsConstructor
public class ColumnResponse {
    private Long id;

    private String name;

    private String description;

    private EssayType type;

    private Long writer;

    private String essayUrl;

    private LocalDateTime createTime;

}
