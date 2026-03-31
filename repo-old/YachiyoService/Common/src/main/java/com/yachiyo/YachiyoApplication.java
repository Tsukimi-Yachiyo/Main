package com.yachiyo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.yachiyo.mapper")
@EnableAsync
public class YachiyoApplication {

    public static void main(String[] args) {
        SpringApplication.run(YachiyoApplication.class, args);
    }

}
