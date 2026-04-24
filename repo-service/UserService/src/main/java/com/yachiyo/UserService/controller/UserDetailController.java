package com.yachiyo.UserService.controller;

import com.yachiyo.UserService.dto.PosterDetailResponse;
import com.yachiyo.UserService.dto.PublicUserDetailResponse;
import com.yachiyo.UserService.dto.SearchDetailResponse;
import com.yachiyo.UserService.dto.SelfUserDetailResponse;
import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserInteractService;
import com.yachiyo.UserService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v2/user")
@RequiredArgsConstructor
@Validated
public class UserDetailController {

    private final UserService userService;

    private final UserInteractService userInteractService;

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
    public Mono<Result<SelfUserDetailResponse>> getUserDetail(@AuthenticationPrincipal String userId) {
        return userService.getUserDetail(Long.parseLong(userId));
    }

    /**
     * 更新用户详情
     *
     * @param selfUserDetailResponse 用户详情
     * @return 是否更新成功
     */
    @PostMapping("/detail/update")
    public Mono<Result<Boolean>> updateUserDetail(@AuthenticationPrincipal String userId, @RequestBody SelfUserDetailResponse selfUserDetailResponse) {
        return userService.updateUserDetail(Long.parseLong(userId), selfUserDetailResponse);
    }

    /**
     * 获取发帖人用户详情
     * @return 用户详情
     */
    @PostMapping("/detail/poster/get")
    public Mono<Result<PosterDetailResponse>> getPosterDetail(@RequestParam Long userId) {
        return userService.getPosterDetail(userId);
    }

    /**
     * 获取用户详情
     * @param userId 用户ID
     * @return 是否关注成功
     */
    @PostMapping("/detail/user/detail/get")
    public Mono<Result<PublicUserDetailResponse>> getUserDetail(@RequestParam Long userId) {
        return userInteractService.getOnesDetail(userId);
    }

    /**
     * 获取用户关注列表
     * @return 用户关注列表
     */
    @PostMapping("/detail/user/follow/get")
    public Flux<Result<Long>> getFolloweeList(@AuthenticationPrincipal String userId) {
        return userInteractService.getFolloweeList(Long.parseLong(userId));
    }

    /**
     * 获取用户粉丝列表
     * @return 用户粉丝列表
     */
    @PostMapping("/detail/user/follower/get")
    public Flux<Result<Long>> getFollowerList(@AuthenticationPrincipal String userId) {
        return userInteractService.getFollowerList(Long.parseLong(userId));
    }

    /**
     * 关注用户
     * @param userId 用户ID
     * @return 是否关注成功
     */
    @PostMapping("/detail/user/follow/follow")
    public Mono<Result<Boolean>> follow(@AuthenticationPrincipal String userId, @RequestParam Long followeeId) {
        return userInteractService.follow(Long.parseLong(userId), followeeId);
    }

    /**
     * 获取用户关注状态
     * @param userId 用户ID
     * @return 是否关注成功
     */
    @PostMapping("/detail/user/follow/status/get")
    public Mono<Result<Boolean>> getFollowStatus(@AuthenticationPrincipal String userId, @RequestParam Long followeeId) {
        return userInteractService.getFollowStatus(Long.parseLong(userId), followeeId);
    }

    /**
     * 搜索用户
     * @param userName 用户名
     * @return 用户搜索结果
     */
    @PostMapping("/detail/user/search")
    public Flux<Result<SearchDetailResponse>> searchUser(@AuthenticationPrincipal String userId, @RequestParam String userName, @RequestParam int pageNum, @RequestParam int pageSize) {
        return userInteractService.searchUser(Long.parseLong(userId), userName, pageNum, pageSize);
    }
}
