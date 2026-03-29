package com.yachiyo.controller;

import com.yachiyo.result.Result;
import com.yachiyo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/coin")
@RequiredArgsConstructor
@Validated
public class FujuController {

    private final UserService userService;

    /**
     * 签到接口
     */
    @PostMapping("/sign")
    public Result<Boolean> sign() {
        return userService.sign();
    }

}
