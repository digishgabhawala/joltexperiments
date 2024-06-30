package com.drg.joltexperiments.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestConfig {

    @Bean
    public Map<String, ServiceConfigDetails> serviceConfigMap() {
        // Create a mock serviceConfigMap with dummy data and schema
        Map<String, ServiceConfigDetails> serviceConfigMap = new HashMap<>();
        serviceConfigMap.put("service1", new ServiceConfigDetails(
                "http://localhost:8080/service1",
                "http://localhost:8080/v3/api-docs",
                getMockSchemaJson())
        );
        return serviceConfigMap;
    }

    private String getMockSchemaJson() {
        return "{\n" +
                "  \"type\": \"array\",\n" +
                "  \"items\": {\n" +
                "    \"$ref\": \"#/components/schemas/Customer\"\n" +
                "  },\n" +
                "  \"components\": {\n" +
                "    \"schemas\": {\n" +
                "      \"Customer\": {\n" +
                "        \"type\": \"object\",\n" +
                "        \"properties\": {\n" +
                "          \"id\": {\n" +
                "            \"type\": \"integer\",\n" +
                "            \"format\": \"int64\"\n" +
                "          },\n" +
                "          \"firstName\": {\n" +
                "            \"type\": \"string\"\n" +
                "          },\n" +
                "          \"lastName\": {\n" +
                "            \"type\": \"string\"\n" +
                "          },\n" +
                "          \"email\": {\n" +
                "            \"type\": \"string\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}
