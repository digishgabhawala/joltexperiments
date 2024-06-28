package com.drg.joltexperiments.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceUrlConfig {

    @Bean
    public Map<String, String> serviceUrlMap() {
        Map<String, String> serviceMap = new HashMap<>();
        serviceMap.put("customers", "http://localhost:9003/api/customers");
        serviceMap.put("cards", "http://localhost:9004/api/cards");
        serviceMap.put("accounts", "http://localhost:9005/api/accounts");
        serviceMap.put("kyc", "http://localhost:9006/api/kyc");
        return serviceMap;
    }
}
