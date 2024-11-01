// Removed Mono imports and reactive types
package com.drg.joltexperiments.bff;

import com.drg.joltexperiments.bff.steps.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@RestController
@RequestMapping("/bff")
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @Autowired
    private StepFactory stepFactory;

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
            StepInteface si = stepFactory.createStep(step);
            result = si.execute(headers, body, step, stepResults);
        }
        return result;
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


}
