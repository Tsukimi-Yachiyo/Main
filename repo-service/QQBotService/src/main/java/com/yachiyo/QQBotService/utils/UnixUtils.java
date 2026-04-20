package com.yachiyo.QQBotService.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class UnixUtils {
    public LocalDateTime ofMilli(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneOffset.UTC // UTC+0
        );
    }

    public LocalDateTime ofSecond(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneOffset.UTC // UTC+0
        );
    }
}
