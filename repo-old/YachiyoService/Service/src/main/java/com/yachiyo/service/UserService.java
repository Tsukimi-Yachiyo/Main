package com.yachiyo.service;

import com.yachiyo.dto.UserDetailResponse;
import com.yachiyo.result.Result;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    /**
     * 获取用户详情
     * @return 用户详情
     */
    Result<UserDetailResponse> getUserDetail();

    /**
     * 更新用户详情
     * @param userDetailResponse 用户详情
     * @return 是否更新成功
     */
     Result<Boolean> updateUserDetail( UserDetailResponse userDetailResponse);

    /**
     * 更新用户头像
     * @param userAvatar 用户头像
     * @return 是否更新成功
     */
     Result<Boolean> updateUserAvatar(MultipartFile userAvatar);

     /**
     * 获取用户头像
     * @return 用户头像
     */
     Result<byte[]> getUserAvatar();
}
