package com.drg.joltexperiments.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceSchemaConfig {

    @Bean
    public Map<String, String> serviceSchemaMap() {
        Map<String, String> schemaMap = new HashMap<>();
        schemaMap.put("customers", "{\n" +
                "  \"schema\": {\n" +
                "    \"type\": \"array\",\n" +
                "    \"items\": {\n" +
                "      \"$ref\": \"#/components/schemas/Customer\"\n" +
                "    }\n" +
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
                "}");
        return schemaMap;
    }
}
