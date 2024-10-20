package com.drg.joltexperiments.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class ServiceConfig {
    @Bean
    public Map<String, ServiceConfigDetails> serviceConfigMap() {

        Map<String, ServiceConfigDetails> serviceMap = new LinkedHashMap<>();

        // Full schema for the Customer object
        String customerSchema = "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"integer\",\n" +
                "      \"format\": \"int64\"\n" +
                "    },\n" +
                "    \"firstName\": {\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    \"lastName\": {\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    \"email\": {\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n" +
                "}";

        // Configuration for a specific customer by ID (GET /customers/{id})
        serviceMap.put("customers/{id}", new ServiceConfigDetails(
                "http://localhost:9003/api/customers/{id}",
                "http://localhost:9003/v3/api-docs",
                customerSchema
        ));

        // Configuration for all customers (GET /customers)
        serviceMap.put("customers", new ServiceConfigDetails(
                "http://localhost:9003/api/customers",
                "http://localhost:9003/v3/api-docs",
                "{\n" +
                        "  \"type\": \"array\",\n" +
                        "  \"items\": " + customerSchema + "\n" +
                        "}"
        ));

        return serviceMap;
    }
}

class ServiceConfigDetails {
    private String serviceUrl;
    private String schemaUrl;
    private String schema;

    public ServiceConfigDetails(String serviceUrl, String schemaUrl, String schema) {
        this.serviceUrl = serviceUrl;
        this.schemaUrl = schemaUrl;
        this.schema = schema;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public String getSchemaUrl() {
        return schemaUrl;
    }

    public String getSchema() {
        return schema;
    }
}
