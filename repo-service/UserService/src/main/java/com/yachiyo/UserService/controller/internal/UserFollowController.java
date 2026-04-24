package com.yachiyo.UserService.controller.internal;

import com.yachiyo.UserService.result.Result;
import com.yachiyo.UserService.service.UserInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/internal/user/follow/")
public class UserFollowController {

    @Autowired
    private UserInteractService userInteractService;

    @GetMapping("isFriend")
    public Mono<Result<Boolean>> isFriend(@RequestParam Long currentUserId, @RequestParam Long followeeId) {
        return userInteractService.isFriend(currentUserId, followeeId);
    }

    @PostMapping("friends")
    public Flux<Result<Long>> friends(@RequestParam Long currentUserId) {
        return userInteractService.friends(currentUserId);
    }
}
