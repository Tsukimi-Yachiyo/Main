package com.yachiyo.ColumnService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.yachiyo.ColumnService.client")
@MapperScan("com.yachiyo.ColumnService.mapper")
@EnableDiscoveryClient
@EnableCaching
public class ColumnServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColumnServiceApplication.class, args);
    }
}
