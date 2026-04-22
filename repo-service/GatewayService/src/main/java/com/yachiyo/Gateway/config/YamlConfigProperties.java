package com.yachiyo.Gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class YamlConfigProperties {

    private List<String> openApi; // 注意：驼峰命名，自动匹配横杠配置
}
