package com.yachiyo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class UserDetailResponse {

    @Schema(description = "用户名")
    String userName;

    @Schema(description = "简介")
    String userIntroduction;

    @Schema(description = "城市")
    String userCity;

    @Schema(description = "性别")
    String userGender;

    @Schema(description = "年龄")
    String userPhone;

    @Schema(description = "生日")
    Date userBirthday;
}
