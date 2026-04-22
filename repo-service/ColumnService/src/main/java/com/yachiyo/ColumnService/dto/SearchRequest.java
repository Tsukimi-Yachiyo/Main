package com.yachiyo.ColumnService.dto;

import lombok.Data;

@Data
public class SearchRequest {

    private String keyword;
    private Integer pageNum;
    private Integer pageSize;
}
