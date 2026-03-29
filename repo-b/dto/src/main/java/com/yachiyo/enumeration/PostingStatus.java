package com.yachiyo.enumeration;

/**
 * 帖子状态枚举
 */
public enum PostingStatus {

    /**
     * 待审核
     */
    PENDING,

    /**
     * 已审核通过
     */
    APPROVED,

    /**
     * 已拒绝
     */
    REJECTED,

    /**
     * 所有状态（用于查询）
     */
    ALL
}