package com.yachiyo.FileService.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class MinioConfig {

    @Value("${spring.minio.endpoint}")
    private String endpoint;
    @Value("${spring.minio.access-key}")
    private String accessKey;
    @Value("${spring.minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}