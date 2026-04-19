package com.yachiyo.AuthService.config;

import com.github.kwfilter.util.KeyWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Configuration
public class FastMethodConfig {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String generateCode(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int randomNum = (int) (Math.random() * (max - min + 1)) + min;
        return String.valueOf(randomNum);
    }

    @Bean
    public KeyWordFilter keyWordFilter() {
        return KeyWordFilter.getInstance();
    }
}
