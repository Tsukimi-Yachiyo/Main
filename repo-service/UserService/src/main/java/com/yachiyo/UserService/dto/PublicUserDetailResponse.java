package com.yachiyo.UserService.dto;

import lombok.Data;

@Data
public class PublicUserDetailResponse {

    /**
     * 用户介绍
     */
    private String userIntroduction;

    /**
     * 用户城市
     */
    private String userCity;

    /**
     * 用户性别
     */
    private String userGender;
}
