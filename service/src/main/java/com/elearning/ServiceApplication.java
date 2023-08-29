package com.elearning;

import com.elearning.utils.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = {"com.elearning"}, exclude = { SecurityAutoConfiguration.class })
@SpringBootApplication
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
        System.out.println("-----------------------------------------------------------");
        System.out.println("🚀 Api doc ready at : " +
                Constants.SERVICE_URL + "/swagger-ui/index.html?configUrl=/e-learning/api-docs/swagger-config");
    }
}

