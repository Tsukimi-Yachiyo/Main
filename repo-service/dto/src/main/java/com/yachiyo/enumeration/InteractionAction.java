package com.yachiyo.enumeration;

/**
 * 互动操作枚举
 */
public enum InteractionAction {

    /**
     * 添加互动（点赞/收藏）
     */
    ADD,

    /**
     * 移除互动（取消点赞/取消收藏）
     */
    REMOVE,

    /**
     * 切换互动状态（如果已存在则取消，否则添加）
     */
    TOGGLE
}