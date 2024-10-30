// Removed Mono imports and reactive types
package com.drg.joltexperiments.bff;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@RestController
@RequestMapping("/bff")
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @GetMapping("/**")
    public String handleGetRequest(@RequestHeader HttpHeaders headers, ServerHttpRequest request) {
        return processRequest(headers, null, request, "GET");
    }

    @PostMapping("/**")
    public String handlePostRequest(@RequestHeader HttpHeaders headers, @RequestBody String body, ServerHttpRequest request) {
        return processRequest(headers, body, request, "POST");
    }

    private String processRequest(HttpHeaders headers, String body, ServerHttpRequest request, String method) {
        try {
            String path = extractPathFromRequest(request);
            ServiceConfigEntity serviceConfigEntity = getServiceConfigFromPath(path, method);

            Map<String, Object> stepResults = new HashMap<>();
            extractRequestData(serviceConfigEntity, request, body, stepResults);

            // Execute steps sequentially
            return executeSteps(headers, body, request, method, serviceConfigEntity, stepResults);
        } catch (Exception e) {
            logger.error("Error processing {} request: {}", method, e.getMessage());
            throw new IllegalArgumentException("Request failed: " + e.getMessage());
        }
    }

    private String executeSteps(HttpHeaders headers, String body, ServerHttpRequest request, String method, ServiceConfigEntity serviceConfigEntity, Map<String, Object> stepResults) {
        List<Step> steps = serviceConfigEntity.getSteps();
        String result = "";

        for (Step step : steps) {
            switch (step.getType().toLowerCase()) {
                case "renamevariables":
                    renameVariables(step, stepResults);
                    break;

                case "buildbody": // Added buildBody step
                    buildBody(step, stepResults);
                    break;

                case "apicall":
                    result = executeApiCallStep(headers, body, step, stepResults);
                    break;

                case "combineresponses":
                    result = combineResponses(stepResults,step);
                    break;

                default:
                    logger.warn("Unknown step type: {}", step.getType());
            }
        }
        return result;
    }

    //todo: refactor buildbody and renamevariables for common code.


    private void buildBody(Step step, Map<String, Object> stepResults) {
        Map<String, String> renameMappings = step.getRenameMappings();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode bodyNode = mapper.createObjectNode();

        if (renameMappings != null) {
            for (Map.Entry<String, String> entry : renameMappings.entrySet()) {
                String sourcePath = entry.getKey();
                String targetKey = entry.getValue();

                try {
                    if (sourcePath.startsWith("$.") && sourcePath.contains(".")) {
                        // JSON path scenario
                        String[] pathParts = sourcePath.substring(2).split("\\.");
                        Object rootObject = stepResults.get(pathParts[0]);

                        JsonNode currentNode = null;

                        // Check if the root object is itself a string or JSON tree
                        if (rootObject instanceof String) {
                            String rootString = (String) rootObject;
                            // Try parsing the string as JSON
                            if (rootString.trim().startsWith("{") || rootString.trim().startsWith("[")) {
                                currentNode = mapper.readTree(rootString);
                            } else {
                                // Directly use the string if it's not JSON
                                currentNode = mapper.convertValue(rootString, JsonNode.class);
                            }
                        } else {
                            currentNode = mapper.convertValue(rootObject, JsonNode.class);
                        }

                        // Traverse through JSON path if currentNode is a JSON structure
                        for (int i = 1; i < pathParts.length; i++) {
                            if (currentNode != null) {
                                currentNode = currentNode.get(pathParts[i]);
                            }
                        }

                        // Add final node value to body if found
                        if (currentNode != null && !currentNode.isNull()) {
                            bodyNode.putPOJO(targetKey, mapper.convertValue(currentNode, Object.class));
                        } else {
                            logger.warn("Path '{}' not found in JSON structure.", sourcePath);
                        }
                    } else if (stepResults.containsKey(sourcePath)) {
                        // Direct key value scenario
                        bodyNode.putPOJO(targetKey, stepResults.get(sourcePath));
                    } else {
                        logger.warn("Source key '{}' not found in stepResults for body construction", sourcePath);
                    }
                } catch (Exception e) {
                    logger.error("Error extracting value from '{}' for target '{}': {}", sourcePath, targetKey, e.getMessage());
                }
            }
        }

        // Store the final body in stepResults
        stepResults.put(step.getName(), bodyNode.toString());
    }





    private void renameVariables(Step step, Map<String, Object> stepResults) {
        Map<String, String> renameMappings = step.getRenameMappings();
        ObjectMapper mapper = new ObjectMapper();

        for (Map.Entry<String, String> entry : renameMappings.entrySet()) {
            String sourcePath = entry.getKey();
            String targetKey = entry.getValue();

            try {
                // Check if sourcePath is a JSON-like path (e.g., "$.callAccountApi.customerId")
                if (sourcePath.startsWith("$.") && sourcePath.contains(".")) {
                    // Split the path by dots, ignoring the initial "$" character
                    String[] pathParts = sourcePath.substring(2).split("\\.");

                    // Get the top-level JSON object in stepResults
                    Object rootObject = stepResults.get(pathParts[0]);

                    // Initialize currentNode based on the type of rootObject
                    JsonNode currentNode = null;
                    if (rootObject instanceof String) {
                        // Parse the JSON string to a JsonNode if rootObject is a String
                        currentNode = mapper.readTree((String) rootObject);
                    } else {
                        // Convert to JsonNode if it's already a structured object
                        currentNode = mapper.convertValue(rootObject, JsonNode.class);
                    }

                    // Traverse through each part of the path
                    for (int i = 1; i < pathParts.length; i++) {
                        if (currentNode != null) {
                            currentNode = currentNode.get(pathParts[i]);
                        }
                    }

                    // If we reach a valid node, add it to stepResults under the target key
                    if (currentNode != null && !currentNode.isNull()) {
                        stepResults.put(targetKey, mapper.convertValue(currentNode, Object.class));
                    } else {
                        logger.warn("Path '{}' not found in the JSON structure.", sourcePath);
                    }
                } else if (stepResults.containsKey(sourcePath)) {
                    // For simple key renaming
                    stepResults.put(targetKey, stepResults.remove(sourcePath));
                } else {
                    logger.warn("Variable '{}' not found for renaming.", sourcePath);
                }
            } catch (Exception e) {
                logger.error("Error renaming variable from '{}' to '{}': {}", sourcePath, targetKey, e.getMessage());
            }
        }
    }
    private String extractRequestData(ServiceConfigEntity serviceConfigEntity, ServerHttpRequest request, String body, Map<String, Object> stepResults) {
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

        request.getQueryParams().forEach((key, values) -> {
            if (!values.isEmpty()) {
                stepResults.put(key, values.get(0));
            }
        });

        if (body != null && !body.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode bodyJson = mapper.readTree(body);

                bodyJson.fields().forEachRemaining(field -> {
                    JsonNode valueNode = field.getValue();
                    Object value = valueNode.isTextual() ? valueNode.asText()
                            : valueNode.isInt() ? valueNode.asInt()
                            : valueNode.isLong() ? valueNode.asLong()
                            : valueNode.isDouble() ? valueNode.asDouble()
                            : valueNode.isBoolean() ? valueNode.asBoolean()
                            : valueNode.toString();
                    stepResults.put(field.getKey(), value);
                });
            } catch (Exception e) {
                logger.error("Error parsing request body: {}", e.getMessage());
                throw new IllegalArgumentException("Invalid JSON body");
            }
        }
        return "";
    }

    private String executeApiCallStep(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        String url = buildUrlWithVariables(step.getServiceUrl(), stepResults);
        logger.info("Making API call to: {}", url);

        String requestBody = (String) stepResults.getOrDefault(step.getBody(), body); // Use configured body if available

        String response = executeRestTemplateRequest(headers, requestBody, url, step.getMethod(), step.getResponseSchema());
        stepResults.put(step.getName(), response);
        return response;
    }

    private String buildUrlWithVariables(String urlTemplate, Map<String, Object> stepResults) {
        for (Map.Entry<String, Object> entry : stepResults.entrySet()) {
            urlTemplate = urlTemplate.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return urlTemplate;
    }

    //todo: make headers configuraable
    private String executeRestTemplateRequest(HttpHeaders originalHeaders, String body, String url, String method, String responseSchema) {
        RestTemplate restTemplate = new RestTemplate();

        // Create a new HttpHeaders instance and selectively add necessary headers
        HttpHeaders headers = new HttpHeaders();
        originalHeaders.forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Content-Length") &&
                    !key.equalsIgnoreCase("Host") &&
                    !key.equalsIgnoreCase("Transfer-Encoding")) {
                headers.put(key, values); // Copy only necessary headers
            }
        });

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Execute the request with the selected HTTP method
            ResponseEntity<String> response = "POST".equalsIgnoreCase(method) ?
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class) :
                    restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            // Validate response against the provided schema
            validateSchema(responseSchema, response.getBody(), "Response");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred during RestTemplate request: {}", e.getMessage());
            throw e;
        }
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
                throw new IllegalArgumentException(validationType + " validation failed: " +
                        String.join("\n", validationMessages.stream()
                                .map(ValidationMessage::getMessage)
                                .toList()));
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
                continue;
            }
            if (!configParts[i].equals(requestParts[i])) {
                return false;
            }
        }
        return true;
    }

    private String combineResponses(Map<String, Object> stepResults, Step step) {
        try {
            // Get the list of response keys to combine from the Step object
            List<String> responseKeys = step.getCombineResponses();

            // Initialize ObjectMapper for JSON handling
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> combinedResult = new HashMap<>();

            // Iterate through the response keys and retrieve their corresponding values from stepResults
            for (String key : responseKeys) {
                if (stepResults.containsKey(key)) {
                    String response = (String) stepResults.get(key);
                    JsonNode responseJson = mapper.readTree(response);
                    combinedResult.put(key, responseJson); // Use the key for the combined result
                } else {
                    logger.warn("Response key '{}' not found in stepResults", key);
                }
            }

            // Return the combined result as a JSON string
            return mapper.writeValueAsString(combinedResult);
        } catch (Exception e) {
            logger.error("Error combining responses: {}", e.getMessage());
            throw new RuntimeException("Failed to combine responses", e);
        }
    }

}
