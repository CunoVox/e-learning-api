package com.elearning;

import com.elearning.utils.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.elearning"})
@EnableScheduling
@Configuration
@EnableMongoRepositories
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
        System.out.println("-----------------------------------------------------------");
        System.out.println("🚀 Api doc ready at : " +
                Constants.SERVICE_URL + "/swagger-ui/index.html?configUrl=/e-learning/api-docs/swagger-config");
    }
}

