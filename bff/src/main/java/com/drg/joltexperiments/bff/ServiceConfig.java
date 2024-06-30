package com.drg.joltexperiments.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceConfig {

    @Bean
    public Map<String, ServiceConfigDetails> serviceConfigMap() {
        Map<String, ServiceConfigDetails> serviceMap = new HashMap<>();
        serviceMap.put("customers", new ServiceConfigDetails(
                "http://localhost:9003/api/customers",
                "http://localhost:9003/v3/api-docs",
                "{\n" +
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
                        "}")
        );
        // Repeat for other services: cards, accounts, kyc
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
