package com.example.matchingms.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Retryer retryer() {
        return new CustomRetryer();
    }
}
