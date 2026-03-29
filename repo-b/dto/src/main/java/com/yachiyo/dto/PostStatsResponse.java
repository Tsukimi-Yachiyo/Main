package com.yachiyo.dto;

import lombok.Data;

/**
 * 帖子统计响应DTO
 */
@Data
public class PostStatsResponse {

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 收藏数
     */
    private Long collectionCount;

    /**
     * 阅读数
     */
    private Long readingCount;

    /**
     * 金币数
     */
    private Long coinCount;

    /**
     * 当前用户是否点赞
     */
    private Boolean liked;

    /**
     * 当前用户是否收藏
     */
    private Boolean collected;
}