package com.yachiyo.UserService.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data @Accessors(chain = true)
public class PosterDetailResponse {

    public String userName;

    public String userAvatar;
}
