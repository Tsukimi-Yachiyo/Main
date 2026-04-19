package com.yachiyo.UserService.service;

import com.yachiyo.UserService.result.Result;
import reactor.core.publisher.Mono;

public interface UserDetailUpdateService {

    Mono<Result<Boolean>> initUserDetail(Long id);

    Mono<Result<Boolean>> login(Long id);
}
