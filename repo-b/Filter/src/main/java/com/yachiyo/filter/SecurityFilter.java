package com.yachiyo.filter;

import com.yachiyo.Config.SecurityConfig;
import com.yachiyo.Config.YamlConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * 安全过滤器
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilter  extends AbstractHttpConfigurer<SecurityFilter, HttpSecurity> {

    private final YamlConfigProperties yamlConfigProperties;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http) {
        // 将 List 转换为 String[]
        String[] openApiArray = yamlConfigProperties.getOpenApi().toArray(String[]::new);
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(openApiArray).permitAll()
                        .requestMatchers("/api/v3/**").permitAll()
                        .requestMatchers("/api/v2/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (_, _, _) -> {
        };
    }
}
