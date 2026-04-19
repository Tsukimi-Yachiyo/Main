package com.yachiyo.CoinService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.yachiyo.CoinService.mapper")
public class CoinServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoinServiceApplication.class, args);
    }
}
