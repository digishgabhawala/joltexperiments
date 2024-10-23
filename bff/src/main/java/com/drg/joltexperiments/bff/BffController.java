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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            // Initialize a map to store intermediate results for steps
            Map<String, Object> stepResults = new HashMap<>();

            // Execute all the steps in sequence and return the final result
            return executeSteps(headers, body, request, method, serviceConfigEntity, stepResults);
        } catch (Exception e) {
            logger.error("Error processing {} request: {}", method, e.getMessage());
            return Mono.error(new IllegalArgumentException("Request failed: " + e.getMessage()));
        }
    }

    private Mono<String> executeSteps(HttpHeaders headers, String body, ServerHttpRequest request, String method, ServiceConfigEntity serviceConfigEntity, Map<String, Object> stepResults) {
        List<Step> steps = serviceConfigEntity.getSteps();

        // Sequentially execute each step
        Mono<String> result = Mono.just(""); // Starting with an empty result

        for (Step step : steps) {
            if ("apiCall".equalsIgnoreCase(step.getType())) {
                result = result.flatMap(prevResult -> {
                    // Perform the API call for this step and store the result
                    return executeApiCallStep(headers, body, request, method, step, stepResults);
                });
            }
            // Add other types like joltTransform in future
        }

        // Return the result of the last step
        return result;
    }

    private Mono<String> executeApiCallStep(HttpHeaders headers, String body, ServerHttpRequest request, String method, Step step, Map<String, Object> stepResults) {
        String requestPath = extractPathFromRequest(request);
        String url = buildUrlWithQueryParams(requestPath, step, request);

        logger.info("{} request URL: {}", method, url);

        if ("POST".equalsIgnoreCase(method)) {
            validateSchema(step.getRequestSchema(), body, "Request");
        }

        return executeWebClientRequest(headers, body, url, method, step.getResponseSchema())
                .doOnNext(response -> {
                    // Store the result in the stepResults map
                    stepResults.put(step.getName(), response);
                });
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

    private String buildUrlWithQueryParams(String requestPath, Step step, ServerHttpRequest request) {
        String serviceUrl = step.getServiceUrl(); // Base URL from Step
        String stepPath = step.getPath(); // Dynamic path template from Step

        // Replace dynamic placeholders in the stepPath
        String resolvedUrl = resolveDynamicPlaceholders(requestPath, serviceUrl, stepPath);

        // Append query parameters, if present
        String queryParams = request.getURI().getQuery();
        if (queryParams != null && !queryParams.isEmpty()) {
            resolvedUrl += "?" + queryParams;
        }

        return resolvedUrl;
    }

    private String resolveDynamicPlaceholders(String requestPath, String serviceUrl, String stepPath) {
        String[] requestPathParts = requestPath.split("/");
        String[] stepPathParts = stepPath.split("/");

        // Replace each placeholder in the stepPath with the corresponding value from requestPath
        for (int i = 0; i < stepPathParts.length; i++) {
            if (stepPathParts[i].startsWith("{") && stepPathParts[i].endsWith("}")) {
                String placeholder = stepPathParts[i].substring(1, stepPathParts[i].length() - 1); // Extract placeholder name
                if (i < requestPathParts.length) {
                    serviceUrl = serviceUrl.replace("{" + placeholder + "}", requestPathParts[i]);
                }
            }
        }

        return serviceUrl;
    }

    private ServiceConfigEntity getServiceConfigFromPath(String path, String method) {
        return serviceConfigRepository.findAll().stream()
                .filter(config -> isMatchingPath(config.getPath(), path) && method.equalsIgnoreCase(config.getMethod()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching service found for path: " + path + " and method: " + method));
    }

    private boolean isMatchingPath(String configPath, String requestPath) {
        String[] configParts = configPath.split("/");
        String[] requestParts = requestPath.split("/");

        if (configParts.length != requestParts.length) {
            return false;
        }

        for (int i = 0; i < configParts.length; i++) {
            if (configParts[i].startsWith("{") && configParts[i].endsWith("}")) {
                continue; // Dynamic part of path, skip comparison
            }
            if (!configParts[i].equals(requestParts[i])) {
                return false;
            }
        }
        return true;
    }
}
