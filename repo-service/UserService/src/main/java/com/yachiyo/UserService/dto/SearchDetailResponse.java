package com.yachiyo.UserService.dto;

import lombok.Data;

@Data
public class SearchDetailResponse {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 关注者数
     */
    private Long followerCount;

    /**
     * 是否关注
     */
    private Boolean isFollowing;

    /**
     * 是否被关注
     */
    private Boolean isFollowed;
}
