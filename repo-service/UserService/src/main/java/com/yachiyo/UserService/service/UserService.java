package com.yachiyo.UserService.service;

import com.yachiyo.UserService.dto.PosterDetailResponse;
import com.yachiyo.UserService.dto.SelfUserDetailResponse;
import com.yachiyo.UserService.result.Result;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface UserService {

    /**
     * 获取用户详情
     * @return 用户详情
     */
    Mono<Result<SelfUserDetailResponse>> getUserDetail(Long userId);

    /**
     * 更新用户详情
     * @param selfUserDetailResponse 用户详情
     * @return 是否更新成功
     */
     Mono<Result<Boolean>> updateUserDetail(Long userId, SelfUserDetailResponse selfUserDetailResponse);

    /**
     * 更新用户头像
     * @param userAvatar 用户头像
     * @return 是否更新成功
     */
     Mono<Result<Boolean>> updateUserAvatar(Long userId, FilePart userAvatar);

     /**
     * 获取用户头像
     * @return 用户头像
     */
     Mono<Result<String>> getUserAvatar(Long userId);

     /**
     * 获取某用户详情
     * @param userId 用户ID
     * @return 用户详情
     */
     Mono<Result<PosterDetailResponse>> getPosterDetail(Long userId);
}
