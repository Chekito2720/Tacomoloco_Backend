package com.tacomoloco.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaConfig {

    @Bean
    public String eurekaInfo() {
        return "Eureka Server running on port 8761";
    }
}
