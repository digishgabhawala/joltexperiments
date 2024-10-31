package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;

public interface StepInteface {
    static final Logger logger = LoggerFactory.getLogger(ApiCallStep.class);
    String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults);
    default Optional<Object> extractJsonPathValue(String sourcePath, Map<String, Object> stepResults, ObjectMapper mapper) {
        try {
            // JSON path scenario with dot notation
            if (sourcePath.startsWith("$.") && sourcePath.contains(".")) {
                String[] pathParts = sourcePath.substring(2).split("\\.");
                Object rootObject = stepResults.get(pathParts[0]);

                // Initialize currentNode based on the type of rootObject
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
                } else if (rootObject != null) {
                    // Convert non-string root objects to JsonNode
                    currentNode = mapper.convertValue(rootObject, JsonNode.class);
                }

                // Traverse JSON path
                for (int i = 1; i < pathParts.length && currentNode != null; i++) {
                    currentNode = currentNode.get(pathParts[i]);
                }

                // Return the found node, or an empty Optional if not found
                return Optional.ofNullable(currentNode);
            } else if (stepResults.containsKey(sourcePath)) {
                // Direct key scenario
                return Optional.ofNullable(stepResults.get(sourcePath));
            }
        } catch (Exception e) {
            logger.error("Error extracting value for '{}': {}", sourcePath, e.getMessage());
        }
        return Optional.empty();
    }

}
