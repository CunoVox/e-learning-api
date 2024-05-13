package com.elearning.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
@Slf4j
public class CorsConfig implements WebMvcConfigurer {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
//        config.addAllowedOrigin("*"); // Cấu hình origins cho phù hợp với yêu cầu của bạn
        config.addAllowedHeader("*"); // Chấp nhận tất cả các header
        config.addAllowedMethod("*"); // Chấp nhận tất cả các phương thức HTTP
        source.registerCorsConfiguration("/**", config);
        log.info("CORS filter is registered");
        return new CorsFilter(source);
    }
}
