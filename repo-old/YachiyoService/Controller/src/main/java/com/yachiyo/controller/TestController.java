package com.yachiyo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/test")
@RequiredArgsConstructor
@Validated
public class TestController {

    /**
     * 测试接口
     * @return 测试字符串
     */
    @GetMapping("/hello")
    public String Hello(){
        return "Hello World!";
    }
}
