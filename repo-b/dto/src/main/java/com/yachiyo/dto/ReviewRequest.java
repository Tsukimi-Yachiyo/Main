package com.yachiyo.dto;

import com.yachiyo.enumeration.ReviewAction;
import lombok.Data;

/**
 * 审核请求DTO
 */
@Data
public class ReviewRequest {

    /**
     * 帖子ID
     */
    private Long postingId;

    /**
     * 审核操作
     */
    private ReviewAction action;

    /**
     * 拒绝原因（可选）
     */
    private String reason;
}