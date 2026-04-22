package com.yachiyo.QQBotService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.yachiyo.QQBotService.client")
@EnableDiscoveryClient
@MapperScan("com.yachiyo.QQBotService.mapper")
public class QqBotServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QqBotServiceApplication.class, args);
    }
}
