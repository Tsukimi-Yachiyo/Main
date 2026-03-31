package com.yachiyo.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component @Data // 关键：配置修改后刷新该 Bean
@ConfigurationProperties(prefix = "custom.config")  // 绑定配置前缀
public class CustomConfig {
    // 定时间隔（毫秒）
    private String interval;
    // 功能开关
    private boolean enable;
    // 自定义消息
    private String message;
}
