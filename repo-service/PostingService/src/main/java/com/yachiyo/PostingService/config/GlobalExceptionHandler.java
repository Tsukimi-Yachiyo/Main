package com.yachiyo.PostingService.config;

import com.yachiyo.PostingService.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {

        return Result.error("500", "系统异常：" + e.getMessage());
    }
}
