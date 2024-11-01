package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Map;

public class AddVariablesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(AddVariablesStep.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        Map<String, String> mappings = step.getMappings();

        if (mappings != null) {
            mappings.forEach((jsonPath, value) -> addJsonPathValue(jsonPath, value, stepResults));
        }
        return "";
    }

    private void addJsonPathValue(String jsonPath, String value, Map<String, Object> stepResults) {
        try {
            if (jsonPath.startsWith("$.") && jsonPath.contains(".")) {
                String[] pathParts = jsonPath.substring(2).split("\\.");
                Object rootObject = stepResults.get(pathParts[0]);

                // Check if the root object is a JSON-like string and parse it if necessary
                ObjectNode currentNode = parseToJsonNode(rootObject);

                // Traverse and/or create nodes along the JSON path
                ObjectNode parentNode = currentNode;
                for (int i = 1; i < pathParts.length - 1; i++) {
                    String part = pathParts[i];
                    if (parentNode.has(part) && parentNode.get(part).isObject()) {
                        parentNode = (ObjectNode) parentNode.get(part);
                    } else {
                        ObjectNode newNode = mapper.createObjectNode();
                        parentNode.set(part, newNode);
                        parentNode = newNode;
                    }
                }

                // Set the final value at the target path
                parentNode.put(pathParts[pathParts.length - 1], value);
                if (rootObject instanceof String) {
                    // Convert updated JSON node back to a string
                    stepResults.put(pathParts[0], mapper.writeValueAsString(currentNode));
                } else {
                    // Directly update as an object if it was originally non-string
                    stepResults.put(pathParts[0], mapper.treeToValue(currentNode, Object.class));
                }
            } else {
                // For top-level keys, add directly to stepResults
                stepResults.put(jsonPath, value);
            }
        } catch (Exception e) {
            logger.error("Error adding value for '{}': {}", jsonPath, e.getMessage());
        }
    }

    // Helper method to parse the root object to a JSON node if it's a JSON-like string
    private ObjectNode parseToJsonNode(Object rootObject) {
        ObjectNode currentNode = mapper.createObjectNode();

        if (rootObject instanceof String) {
            String rootString = (String) rootObject;
            try {
                // Parse JSON-like strings
                if (rootString.trim().startsWith("{") || rootString.trim().startsWith("[")) {
                    currentNode = (ObjectNode) mapper.readTree(rootString);
                } else {
                    // Convert simple strings to JSON node format
                    currentNode.put("value", rootString);
                }
            } catch (Exception e) {
                logger.error("Error parsing JSON string '{}': {}", rootString, e.getMessage());
            }
        } else if (rootObject != null) {
            // Convert non-string root objects to ObjectNode
            currentNode = mapper.convertValue(rootObject, ObjectNode.class);
        }
        return currentNode;
    }
}
