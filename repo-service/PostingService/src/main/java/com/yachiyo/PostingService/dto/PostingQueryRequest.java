package com.yachiyo.PostingService.dto;

import com.yachiyo.PostingService.enumeration.PostingStatus;
import lombok.Data;

/**
 * 帖子查询请求DTO
 */
@Data
public class PostingQueryRequest {

    /**
     * 帖子状态（用于筛选）
     */
    private PostingStatus status;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;
}