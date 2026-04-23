package com.yachiyo.UserService.dto;

import com.yachiyo.UserService.tool.SensitiveWordFilter;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;

@Data @Accessors(chain = true)
public class SelfUserDetailResponse {

    @SensitiveWordFilter(message = "用户名包含敏感词")
    String userName;

    @SensitiveWordFilter(message = "用户介绍包含敏感词")
    String userIntroduction;

    @SensitiveWordFilter(message = "城市包含敏感词")
    String userCity;

    String userGender;

    String userPhone;

    Date userBirthday;
}
