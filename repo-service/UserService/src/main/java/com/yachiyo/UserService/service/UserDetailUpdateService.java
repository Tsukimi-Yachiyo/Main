package com.yachiyo.UserService.service;

import com.yachiyo.UserService.result.Result;
import reactor.core.publisher.Mono;

public interface UserDetailUpdateService {

    /**
     * 初始化用户详情
     * @param id 用户ID
     * @return 是否初始化成功
     */
    Mono<Result<Boolean>> initUserDetail(Long id);

    /**
     * 登录用户
     * @param id 用户ID
     * @return 是否登录成功
     */
    Mono<Result<Boolean>> login(Long id);
}
