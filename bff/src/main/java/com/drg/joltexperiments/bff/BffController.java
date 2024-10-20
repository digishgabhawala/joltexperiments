package com.drg.joltexperiments.bff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import java.util.Set;
import java.util.Optional;

@RestController
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @GetMapping("/bff/**")
    public Mono<String> getGeneric(@RequestHeader HttpHeaders headers, ServerHttpRequest request) {
        String path = extractPathFromRequest(request);

        ServiceConfigEntity serviceConfigEntity = getServiceConfigFromPath(path);
        String url = getServiceUrlFromPath(path, serviceConfigEntity);

        String queryParams = request.getURI().getQuery();
        if (queryParams != null && !queryParams.isEmpty()) {
            url += "?" + queryParams;
        }

        logger.info("GET request URL: {}", url);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> validateResponseSchema(serviceConfigEntity, response));
    }

    @PostMapping("/bff/**")
    public Mono<String> postGeneric(@RequestHeader HttpHeaders headers, @RequestBody String body, ServerHttpRequest request) {
        String path = extractPathFromRequest(request);

        ServiceConfigEntity serviceConfigEntity = getServiceConfigFromPath(path);
        String url = getServiceUrlFromPath(path, serviceConfigEntity);

        String queryParams = request.getURI().getQuery();
        if (queryParams != null && !queryParams.isEmpty()) {
            url += "?" + queryParams;
        }

        logger.info("POST request URL: {}", url);

        return webClientBuilder.build()
                .post()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> validateResponseSchema(serviceConfigEntity, response));
    }

    private String extractPathFromRequest(ServerHttpRequest request) {
        String requestPath = request.getPath().pathWithinApplication().value();
        return requestPath.substring("/bff/".length());
    }

    private ServiceConfigEntity getServiceConfigFromPath(String path) {
        // Fetch all service configurations and find the matching one for the given path
        return serviceConfigRepository.findAll().stream()
                .filter(config -> isMatchingPath(config.getPath(), path))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching service found for path: " + path));
    }

    private boolean isMatchingPath(String configPath, String requestPath) {
        // Convert all placeholders like {userId}, {orderId}, etc., to regex patterns (e.g., \w+ for word characters)
        String regexPath = configPath.replaceAll("\\{[^/]+\\}", "[^/]+"); // Matches any sequence of characters except '/'

        // Check if the requestPath matches the regex pattern
        return requestPath.matches(regexPath);
    }

    private String getServiceUrlFromPath(String path, ServiceConfigEntity serviceConfigEntity) {
        String serviceUrl = serviceConfigEntity.getServiceUrl();

        // Handle dynamic placeholders in the service URL
        String[] pathParts = path.split("/");
        String[] serviceParts = serviceConfigEntity.getPath().split("/");

        for (int i = 0; i < serviceParts.length; i++) {
            if (serviceParts[i].startsWith("{") && serviceParts[i].endsWith("}")) {
                // Extract the dynamic part name (e.g., "id" from "{id}")
                String placeholder = serviceParts[i].substring(1, serviceParts[i].length() - 1);

                // Check if there is a corresponding part in the path
                if (i < pathParts.length) {
                    String dynamicValue = pathParts[i];

                    // Replace the placeholder in the service URL
                    serviceUrl = serviceUrl.replace("{" + placeholder + "}", dynamicValue);
                }
            }
        }

        return serviceUrl;
    }

    private void validateResponseSchema(ServiceConfigEntity serviceConfigEntity, String response) {
        String schemaJson = serviceConfigEntity.getSchema();
        if (schemaJson == null) {
            throw new IllegalArgumentException("No schema found for service: " + serviceConfigEntity.getPath());
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode schemaNode = mapper.readTree(schemaJson);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance();
            JsonSchema schema = factory.getSchema(schemaNode);

            JsonNode responseNode = mapper.readTree(response);
            Set<ValidationMessage> validationMessages = schema.validate(responseNode);

            if (!validationMessages.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder("Response validation failed:");
                for (ValidationMessage message : validationMessages) {
                    errorMessages.append("\n").append(message.getMessage());
                }
                throw new IllegalArgumentException(errorMessages.toString());
            }

            logger.info("Response is valid according to the schema.");
        } catch (Exception e) {
            logger.error("Response validation failed: " + e.getMessage());
            throw new IllegalArgumentException("Response validation failed.", e);
        }
    }
}
