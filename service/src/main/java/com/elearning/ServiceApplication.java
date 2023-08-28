package com.elearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.elearning"}, exclude = { SecurityAutoConfiguration.class })
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
//        System.out.println("-----------------------------------------------------------");
//        System.out.println("🚀 Server ready at http://localhost:8080");
//        System.out.println("🚀 Api doc ready at http://localhost:8080/swagger-ui.html ");
    }
}

