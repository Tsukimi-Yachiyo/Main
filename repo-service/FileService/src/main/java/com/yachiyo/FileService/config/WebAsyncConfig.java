package com.yachiyo.FileService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAsyncConfig implements WebMvcConfigurer {

    /**
     * 配置异步请求超时时间
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 与配置文件一致：1小时超时
        configurer.setDefaultTimeout(3600000);
    }
}