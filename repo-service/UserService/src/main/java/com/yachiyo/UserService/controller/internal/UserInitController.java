package com.yachiyo.UserService.controller.internal;

import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserDetailUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/user/detail/")
public class UserInitController {

    @Autowired
    private UserDetailUpdateService userDetailUpdateService;

    @RequestMapping("init/{id}")
    public Mono<Result<Boolean>> initUserDetail(@PathVariable Long id) {
        return userDetailUpdateService.initUserDetail(id);
    }

    @RequestMapping("login/{id}")
    public Mono<Result<Boolean>> login(@PathVariable Long id) {
        return userDetailUpdateService.login(id);
    }
}
