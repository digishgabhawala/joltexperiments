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

            // Map to store intermediate results, including extracted variables
            Map<String, Object> stepResults = new HashMap<>();

            // Execute steps sequentially
            return executeSteps(headers, body, request, method, serviceConfigEntity, stepResults);
        } catch (Exception e) {
            logger.error("Error processing {} request: {}", method, e.getMessage());
            return Mono.error(new IllegalArgumentException("Request failed: " + e.getMessage()));
        }
    }

    private Mono<String> executeSteps(HttpHeaders headers, String body, ServerHttpRequest request, String method, ServiceConfigEntity serviceConfigEntity, Map<String, Object> stepResults) {
        List<Step> steps = serviceConfigEntity.getSteps();

        // Start with an empty result
        Mono<String> result = Mono.just("");

        for (Step step : steps) {
            switch (step.getType().toLowerCase()) {
                case "extractvariables":
                    // Extract path variables and store them in stepResults
                    result = result.flatMap(prevResult -> extractPathAndQueryVariables(step, request, stepResults));
                    break;

                case "renamevariables":
                    // Rename variables and update stepResults
                    result = result.flatMap(prevResult -> renameVariables(step, stepResults));
                    break;

                case "apicall":
                    // Perform the API call for this step, using the extracted variables
                    result = result.flatMap(prevResult -> executeApiCallStep(headers, body, step, stepResults));
                    break;
                case "combineresponses":
                    result = result.flatMap(prevResult -> combineResponses(stepResults));
                    break;
                default:
                    logger.warn("Unknown step type: {}", step.getType());
            }
        }

        // Return the final result (last API call or combined response)
        return result;
    }

    private Mono<String> renameVariables(Step step, Map<String, Object> stepResults) {
        Map<String, String> renameMappings = step.getRenameMappings(); // Assume the rename mappings are provided in the step

        // Iterate through each rename mapping and update the stepResults
        for (Map.Entry<String, String> entry : renameMappings.entrySet()) {
            String originalName = entry.getKey();
            String newName = entry.getValue();

            if (stepResults.containsKey(originalName)) {
                stepResults.put(newName, stepResults.remove(originalName));
            } else {
                logger.warn("Variable '{}' not found for renaming.", originalName);
            }
        }

        return Mono.just(""); // No immediate output, just store renamed variables
    }

    private Mono<String> extractPathAndQueryVariables(Step step, ServerHttpRequest request, Map<String, Object> stepResults) {
        String requestPath = extractPathFromRequest(request);
        String[] requestPathParts = requestPath.split("/");

        // Extract path variables
        String[] stepPathParts = step.getPath().split("/");
        for (int i = 0; i < stepPathParts.length; i++) {
            if (stepPathParts[i].startsWith("{") && stepPathParts[i].endsWith("}")) {
                String variableName = stepPathParts[i].substring(1, stepPathParts[i].length() - 1);
                if (i < requestPathParts.length) {
                    stepResults.put(variableName, requestPathParts[i]); // Store extracted variable in stepResults
                }
            }
        }

        // Extract query parameters and add them to stepResults
        request.getQueryParams().forEach((key, values) -> {
            if (!values.isEmpty()) {
                stepResults.put(key, values.get(0)); // Assuming single-valued query parameters
            }
        });

        return Mono.just(""); // No immediate output, just store variables
    }

    private Mono<String> executeApiCallStep(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        // Build the API call URL by replacing placeholders with extracted variables
        String url = buildUrlWithVariables(step.getServiceUrl(), stepResults);

        logger.info("Making API call to: {}", url);

        return executeWebClientRequest(headers, body, url, step.getMethod(), step.getResponseSchema())
                .doOnNext(response -> {
                    // Store the API call result in stepResults
                    stepResults.put(step.getName(), response);
                });
    }

    private String buildUrlWithVariables(String urlTemplate, Map<String, Object> stepResults) {
        for (Map.Entry<String, Object> entry : stepResults.entrySet()) {
            urlTemplate = urlTemplate.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return urlTemplate;
    }

    private Mono<String> executeWebClientRequest(HttpHeaders headers, String body, String url, String method, String responseSchema) {
        WebClient.RequestHeadersSpec<?> requestSpec;

        // Build WebClient request
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

    private Mono<String> combineResponses(Map<String, Object> stepResults) {
        String customerResponse = (String) stepResults.get("callCustomerApi");
        String accountResponse = (String) stepResults.get("callAccountApi");

        // Combine the customer and account responses
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> combinedResult = new HashMap<>();

        try {
            // Convert responses to JSON objects
            JsonNode customerJson = mapper.readTree(customerResponse);
            JsonNode accountJson = mapper.readTree(accountResponse);

            // Merge the customer and account objects
            combinedResult.put("customer", customerJson);
            combinedResult.put("account", accountJson);

            // Return the combined result as a JSON string
            return Mono.just(mapper.writeValueAsString(combinedResult));
        } catch (Exception e) {
            logger.error("Error combining responses: {}", e.getMessage());
            return Mono.error(new IllegalArgumentException("Error combining responses: " + e.getMessage()));
        }
    }
}