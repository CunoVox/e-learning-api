package com.elearning;

import com.elearning.utils.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
        System.out.println("-----------------------------------------------------------");
        System.out.println("🚀 Api doc ready at : " +
                Constants.SERVICE_URL + "/swagger-ui/index.html?configUrl=/e-learning/api-docs/swagger-config");
    }
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOriginPatterns("http://localhost:5173")
//                        .allowCredentials(true)
//                        .allowedHeaders("*")
//                        .allowedMethods("*");
//            }
//        };
//    }

}

