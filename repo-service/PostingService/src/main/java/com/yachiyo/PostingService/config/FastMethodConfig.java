package com.yachiyo.PostingService.config;

import com.github.kwfilter.util.KeyWordFilter;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FastMethodConfig {

    @Bean
    public KeyWordFilter keyWordFilter() {
        return KeyWordFilter.getInstance();
    }

}
