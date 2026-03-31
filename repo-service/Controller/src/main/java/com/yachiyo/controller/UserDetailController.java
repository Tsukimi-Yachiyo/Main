package com.yachiyo.controller;

import com.yachiyo.dto.PosterDetailResponse;
import com.yachiyo.dto.UserDetailResponse;
import com.yachiyo.result.Result;
import com.yachiyo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/user/detail")
@RequiredArgsConstructor
@Validated
public class UserDetailController {

    @Autowired
    private UserService userService;

    /**
     * 更新用户头像
     *
     * @param avatar 头像
     * @return 是否更新成功
     */
    @PostMapping("/avatar/update")
    public Result<Boolean> updateAvatar(@RequestBody MultipartFile avatar) {
        return userService.updateUserAvatar(avatar);
    }

    /**
     * 获取用户头像
     * @return 用户头像
     */
     @PostMapping("/avatar/get")
    public Result<String> getUserAvatar() {
        return userService.getUserAvatar();
    }

    /**
     * 获取用户详情
     * @return 用户详情
     */
    @PostMapping("/detail/get")
    public Result<UserDetailResponse> getUserDetail() {
        return userService.getUserDetail();
    }

    /**
     * 更新用户详情
     *
     * @param userDetailResponse 用户详情
     * @return 是否更新成功
     */
    @PostMapping("/detail/update")
    public Result<Boolean> updateUserDetail(@RequestBody UserDetailResponse userDetailResponse) {
        return userService.updateUserDetail(userDetailResponse);
    }

    /**
     * 获取某用户详情
     * @return 用户详情
     */
    @PostMapping("/detail/get/user")
    public Result<PosterDetailResponse> getUserDetail(@RequestParam Long userId) {
        return userService.getPosterDetail(userId);
    }
}
