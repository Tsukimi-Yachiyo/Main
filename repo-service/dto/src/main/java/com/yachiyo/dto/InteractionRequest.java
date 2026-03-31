package com.yachiyo.dto;

import com.yachiyo.enumeration.InteractionAction;
import com.yachiyo.enumeration.InteractionType;
import lombok.Data;

/**
 * 互动请求DTO
 */
@Data
public class InteractionRequest {

    /**
     * 帖子ID
     */
    private Long postingId;

    /**
     * 互动类型（点赞/收藏）
     */
    private InteractionType type;

    /**
     * 互动操作（添加/移除/切换）
     */
    private InteractionAction action;
}