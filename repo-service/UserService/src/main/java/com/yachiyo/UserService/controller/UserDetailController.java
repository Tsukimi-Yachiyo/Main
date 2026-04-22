package com.yachiyo.UserService.controller;

import com.yachiyo.UserService.dto.PosterDetailResponse;
import com.yachiyo.UserService.dto.UserDetailResponse;
import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
@Validated
public class UserDetailController {

    private final UserService userService;

    /**
     * 更新用户头像
     *
     * @param avatar 头像
     * @return 是否更新成功
     */
    @PostMapping("/avatar/update")
    public Mono<Result<Boolean>> updateAvatar(@AuthenticationPrincipal String userId, @RequestPart("avatar") FilePart avatar) {
        return userService.updateUserAvatar(Long.parseLong(userId), avatar);
    }

    /**
     * 获取用户头像
     * @return 用户头像
     */
     @PostMapping("/avatar/get")
    public Mono<Result<String>> getUserAvatar(@AuthenticationPrincipal String userId) {
        return userService.getUserAvatar(Long.parseLong(userId));
    }

    /**
     * 获取用户详情
     * @return 用户详情
     */
    @PostMapping("/detail/get")
    public Mono<Result<UserDetailResponse>> getUserDetail(@AuthenticationPrincipal String userId) {
        return userService.getUserDetail(Long.parseLong(userId));
    }

    /**
     * 更新用户详情
     *
     * @param userDetailResponse 用户详情
     * @return 是否更新成功
     */
    @PostMapping("/detail/update")
    public Mono<Result<Boolean>> updateUserDetail(@AuthenticationPrincipal String userId, @org.springframework.web.bind.annotation.RequestBody UserDetailResponse userDetailResponse) {
        return userService.updateUserDetail(Long.parseLong(userId), userDetailResponse);
    }

    /**
     * 获取某用户详情
     * @return 用户详情
     */
    @PostMapping("/detail/get/user")
    public Mono<Result<PosterDetailResponse>> getUserDetail(@RequestParam Long userId) {
        return userService.getPosterDetail(userId);
    }
}
