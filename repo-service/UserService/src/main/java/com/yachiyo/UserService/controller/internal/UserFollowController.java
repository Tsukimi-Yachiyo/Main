package com.yachiyo.UserService.controller.internal;

import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/user/follow/")
public class UserFollowController {

    @Autowired
    private UserInteractService userInteractService;

    @GetMapping("isFriend")
    public Mono<Result<Boolean>> isFriend(@PathVariable Long currentUserId, @PathVariable Long followeeId) {
        return userInteractService.isFriend(currentUserId, followeeId);
    }
}
