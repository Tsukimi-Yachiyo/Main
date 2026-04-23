package com.yachiyo.UserService.service;

import com.yachiyo.UserService.dto.PublicUserDetailResponse;
import com.yachiyo.UserService.dto.SearchDetailResponse;
import com.yachiyo.UserService.result.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserInteractService {

    /**
     * 关注用户
     * @param userId 用户ID
     * @return 是否关注成功
     */
    Mono<Result<Boolean>> follow(Long userId, Long followeeId);

    /**
     * 获取用户关注列表
     * @param userId 用户ID
     * @return 用户关注列表
     */
    Flux<Result<List<Long>>> getFolloweeList(Long userId);

    /**
     * 获取用户粉丝列表
     * @param userId 用户ID
     * @return 用户粉丝列表
     */
    Flux<Result<List<Long>>> getFollowerList(Long userId);

    /**
     * 获取用户用户详情
     * @param userId 用户ID
     */
    Mono<Result<PublicUserDetailResponse>> getOnesDetail(Long userId);

    /**
     * 获取用户关注状态
     * @param userId 用户ID
     * @return 是否关注成功
     */
    Mono<Result<Boolean>> getFollowStatus(Long userId, Long followeeId);

    /**
     * 搜索用户
     *
     * @param currentUserId 当前用户ID
     * @param userName 用户名
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户搜索结果
     */
    Flux<Result<List<SearchDetailResponse>>> searchUser(long currentUserId, String userName, int pageNum, int pageSize);

    /**
     * 判断用户是否是好友
     * @param currentUserId 当前用户ID
     * @param followeeId 被关注用户ID
     * @return 是否是好友
     */
    Mono<Result<Boolean>> isFriend(Long currentUserId, Long followeeId);
}
