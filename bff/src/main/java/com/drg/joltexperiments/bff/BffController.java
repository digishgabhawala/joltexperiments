package com.drg.joltexperiments.bff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;

import java.util.*;

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
            extractPathAndQueryVariablesImplicit(serviceConfigEntity,request, body, stepResults);

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
//                    result = result.flatMap(prevResult -> extractPathAndQueryVariables(step, request, body,stepResults));
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

    private Mono<String> extractPathAndQueryVariablesImplicit(ServiceConfigEntity serviceConfigEntity, ServerHttpRequest request, String body, Map<String, Object> stepResults) {
        String stepPath = serviceConfigEntity.getPath();
        if (stepPath != null && !stepPath.isEmpty()) {
            String requestPath = extractPathFromRequest(request);
            String[] requestPathParts = requestPath.split("/");
            String[] stepPathParts = stepPath.split("/");

            for (int i = 0; i < stepPathParts.length; i++) {
                if (stepPathParts[i].startsWith("{") && stepPathParts[i].endsWith("}")) {
                    String variableName = stepPathParts[i].substring(1, stepPathParts[i].length() - 1);
                    if (i < requestPathParts.length) {
                        stepResults.put(variableName, requestPathParts[i]);
                    }
                }
            }
        }


        // Extract query parameters and add them to stepResults
        request.getQueryParams().forEach((key, values) -> {
            if (!values.isEmpty()) {
                stepResults.put(key, values.get(0)); // Assuming single-valued query parameters
            }
        });


        // Extract all key-value pairs from the JSON body with correct types
        if (body != null && !body.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode bodyJson = mapper.readTree(body);

                Iterator<Map.Entry<String, JsonNode>> fields = bodyJson.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    JsonNode valueNode = field.getValue();

                    Object value;
                    if (valueNode.isTextual()) {
                        value = valueNode.asText();
                    } else if (valueNode.isInt()) {
                        value = valueNode.asInt();
                    } else if (valueNode.isLong()) {
                        value = valueNode.asLong();
                    } else if (valueNode.isDouble()) {
                        value = valueNode.asDouble();
                    } else if (valueNode.isBoolean()) {
                        value = valueNode.asBoolean();
                    } else {
                        value = valueNode.toString();  // For objects or arrays, store as JSON string
                    }
                    stepResults.put(field.getKey(), value);
                }
            } catch (Exception e) {
                logger.error("Error parsing request body: {}", e.getMessage());
                return Mono.error(new IllegalArgumentException("Invalid JSON body"));
            }
        }
        return Mono.just(""); // No immediate output, just store variables

    }

        private Mono<String> extractPathAndQueryVariables(Step step, ServerHttpRequest request, String body, Map<String, Object> stepResults) {

        String stepPath = step.getPath();
        if (stepPath != null && !stepPath.isEmpty()) {
            String requestPath = extractPathFromRequest(request);
            String[] requestPathParts = requestPath.split("/");
            String[] stepPathParts = stepPath.split("/");

            for (int i = 0; i < stepPathParts.length; i++) {
                if (stepPathParts[i].startsWith("{") && stepPathParts[i].endsWith("}")) {
                    String variableName = stepPathParts[i].substring(1, stepPathParts[i].length() - 1);
                    if (i < requestPathParts.length) {
                        stepResults.put(variableName, requestPathParts[i]);
                    }
                }
            }
        }


        // Extract query parameters and add them to stepResults
        request.getQueryParams().forEach((key, values) -> {
            if (!values.isEmpty()) {
                stepResults.put(key, values.get(0)); // Assuming single-valued query parameters
            }
        });


        // Extract all key-value pairs from the JSON body with correct types
        if (body != null && !body.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode bodyJson = mapper.readTree(body);

                Iterator<Map.Entry<String, JsonNode>> fields = bodyJson.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    JsonNode valueNode = field.getValue();

                    Object value;
                    if (valueNode.isTextual()) {
                        value = valueNode.asText();
                    } else if (valueNode.isInt()) {
                        value = valueNode.asInt();
                    } else if (valueNode.isLong()) {
                        value = valueNode.asLong();
                    } else if (valueNode.isDouble()) {
                        value = valueNode.asDouble();
                    } else if (valueNode.isBoolean()) {
                        value = valueNode.asBoolean();
                    } else {
                        value = valueNode.toString();  // For objects or arrays, store as JSON string
                    }
                    stepResults.put(field.getKey(), value);
                }
            } catch (Exception e) {
                logger.error("Error parsing request body: {}", e.getMessage());
                return Mono.error(new IllegalArgumentException("Invalid JSON body"));
            }
        }
        return Mono.just(""); // No immediate output, just store variables
    }
    public String sanitize(String input) {
        return input.replaceAll("[^\\x20-\\x7E]", ""); // Keep only printable ASCII characters
    }

    private Mono<String> executeApiCallStep(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        // Build the API call URL by replacing placeholders with extracted variables
        String url = buildUrlWithVariables(step.getServiceUrl(), stepResults);

        url = sanitize(url);
        logger.info("Making API call to: {}", url);

//        return executeWebClientRequest(headers, body, url, step.getMethod(), step.getResponseSchema())
//                .doOnNext(response -> {
//                    // Store the API call result in stepResults
//                    stepResults.put(step.getName(), response);
//                });
        return executeRestTemplateRequest(headers, body, url, step.getMethod(), step.getResponseSchema())
                .doOnNext(response -> {
                    // Store the API call result in stepResults
                    stepResults.put(step.getName(), response);
                });

    }

//    private String executeApiCallStepSync(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
//        // Build the API call URL by replacing placeholders with extracted variables
//        String url = buildUrlWithVariables(step.getServiceUrl(), stepResults);
//
//        // Sanitize URL if needed
//        url = sanitize(url);
//        logger.info("Making API call to: {}", url);
//
//        // Make the API call with RestTemplate
//        String response = executeRestTemplateRequest(headers, body, url, step.getMethod(), step.getResponseSchema());
//
//        // Store the API call result in stepResults
//        stepResults.put(step.getName(), response);
//
//        return response;
//    }


    private String buildUrlWithVariables(String urlTemplate, Map<String, Object> stepResults) {
        for (Map.Entry<String, Object> entry : stepResults.entrySet()) {
            urlTemplate = urlTemplate.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return urlTemplate;
    }

    public static void logUrlCharacters(String url) {
        StringBuilder builder = new StringBuilder("URL Characters: ");
        for (char c : url.toCharArray()) {
            builder.append("\\u").append(String.format("%04x", (int) c)).append(" ");
        }
        System.out.println(builder.toString());
    }

    private Mono<String> executeRestTemplateRequest(HttpHeaders headers, String body, String url, String method, String responseSchema) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        return Mono.fromCallable(() -> {
            try {
                ResponseEntity<String> response;
                if ("POST".equalsIgnoreCase(method)) {
                    response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                } else {
                    response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                }

                // Validate the response schema if needed
                validateSchema(responseSchema, response.getBody(), "Response");

                return response.getBody();
            } catch (Exception e) {
                logger.error("Error occurred during RestTemplate request: {}", e.getMessage());
                throw e;
            }
        });
    }



    private Mono<String> executeWebClientRequest(HttpHeaders headers, String body, String url, String method, String responseSchema) {
        WebClient.RequestHeadersSpec<?> requestSpec;

        logUrlCharacters(url);

        // Build WebClient request
        if ("POST".equalsIgnoreCase(method)) {
            requestSpec = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .headers(h -> h.addAll(headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        } else {
            System.out.println("printing url : " + url);

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