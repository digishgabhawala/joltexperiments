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

@RestController
@RequestMapping("/bff")
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @GetMapping("/**")
    public Mono<String> handleGetRequest(@RequestHeader HttpHeaders headers, ServerHttpRequest request) {
        return processRequest(headers, null, request, "GET");
    }

    @PostMapping("/**")
    public Mono<String> handlePostRequest(@RequestHeader HttpHeaders headers, @RequestBody String body, ServerHttpRequest request) {
        return processRequest(headers, body, request, "POST");
    }

    private Mono<String> processRequest(HttpHeaders headers, String body, ServerHttpRequest request, String method) {
        try {
            String path = extractPathFromRequest(request);
            ServiceConfigEntity serviceConfigEntity = getServiceConfigFromPath(path, method);
            String url = buildUrlWithQueryParams(path, serviceConfigEntity, request);

            logger.info("{} request URL: {}", method, url);

            if ("POST".equalsIgnoreCase(method)) {
                validateSchema(serviceConfigEntity.getRequestSchema(), body, "Request");
            }

            return executeWebClientRequest(headers, body, url, method, serviceConfigEntity.getResponseSchema());
        } catch (Exception e) {
            logger.error("Error processing {} request: {}", method, e.getMessage());
            return Mono.error(new IllegalArgumentException("Request failed: " + e.getMessage()));
        }
    }

    private Mono<String> executeWebClientRequest(HttpHeaders headers, String body, String url, String method, String responseSchema) {
        WebClient.RequestHeadersSpec<?> requestSpec;

        // Build the request spec
        if ("POST".equalsIgnoreCase(method)) {
            requestSpec = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .headers(h -> h.addAll(headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        } else {
            requestSpec = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .headers(h -> h.addAll(headers));
        }

        return requestSpec.retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> validateSchema(responseSchema, response, "Response"))
                .doOnError(e -> logger.error("Error occurred during WebClient request: {}", e.getMessage()));
    }

    private void validateSchema(String schemaJson, String data, String validationType) {
        if (schemaJson == null || schemaJson.isEmpty()) {
            logger.warn("{} schema not found, skipping validation.", validationType);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode schemaNode = mapper.readTree(schemaJson);
            JsonSchema schema = JsonSchemaFactory.getInstance().getSchema(schemaNode);
            JsonNode dataNode = mapper.readTree(data);

            Set<ValidationMessage> validationMessages = schema.validate(dataNode);
            if (!validationMessages.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder(validationType + " validation failed:");
                validationMessages.forEach(message -> errorMessages.append("\n").append(message.getMessage()));
                throw new IllegalArgumentException(errorMessages.toString());
            }
            logger.info("{} is valid according to the schema.", validationType);
        } catch (Exception e) {
            logger.error("{} validation error: {}", validationType, e.getMessage());
            throw new IllegalArgumentException(validationType + " validation error: " + e.getMessage(), e);
        }
    }

    private String extractPathFromRequest(ServerHttpRequest request) {
        return request.getPath().pathWithinApplication().value().substring("/bff/".length());
    }

    private String buildUrlWithQueryParams(String path, ServiceConfigEntity serviceConfigEntity, ServerHttpRequest request) {
        String url = serviceConfigEntity.getServiceUrl();
        String queryParams = request.getURI().getQuery();

        if (queryParams != null && !queryParams.isEmpty()) {
            url += "?" + queryParams;
        }

        return handleDynamicPlaceholdersInUrl(path, url, serviceConfigEntity.getPath());
    }

    private String handleDynamicPlaceholdersInUrl(String path, String url, String serviceConfigPath) {
        String[] pathParts = path.split("/");
        String[] servicePathParts = serviceConfigPath.split("/");

        for (int i = 0; i < servicePathParts.length; i++) {
            if (servicePathParts[i].startsWith("{") && servicePathParts[i].endsWith("}")) {
                String placeholder = servicePathParts[i].substring(1, servicePathParts[i].length() - 1);
                if (i < pathParts.length) {
                    url = url.replace("{" + placeholder + "}", pathParts[i]);
                }
            }
        }
        return url;
    }

    private ServiceConfigEntity getServiceConfigFromPath(String path, String method) {
        return serviceConfigRepository.findAll().stream()
                .filter(config -> isMatchingPath(config.getPath(), path) && method.equalsIgnoreCase(config.getMethod()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching service found for path: " + path + " and method: " + method));
    }

    private boolean isMatchingPath(String configPath, String requestPath) {
        // Split both paths into their components
        String[] configParts = configPath.split("/");
        String[] requestParts = requestPath.split("/");

        // If the lengths don't match, the paths cannot be equivalent
        if (configParts.length != requestParts.length) {
            return false;
        }

        // Compare each part of the path
        for (int i = 0; i < configParts.length; i++) {
            // If the part from the config is a placeholder (e.g., {id}), it matches anything
            if (configParts[i].startsWith("{") && configParts[i].endsWith("}")) {
                continue;
            }
            // Otherwise, the parts must match exactly
            if (!configParts[i].equals(requestParts[i])) {
                return false;
            }
        }

        // If all parts match or placeholders are used, return true
        return true;
    }

}
