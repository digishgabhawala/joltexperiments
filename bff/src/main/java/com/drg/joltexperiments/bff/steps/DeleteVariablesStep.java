package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class DeleteVariablesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(DeleteVariablesStep.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config) {
        List<String> itemsList = step.getItemsList();

        if (itemsList != null) {
            itemsList.forEach(jsonPath -> deleteJsonPathValue(jsonPath, stepResults));
        }
        return "";
    }

    private void deleteJsonPathValue(String jsonPath, Map<String, Object> stepResults) {
        try {
            if (jsonPath.startsWith("$.") && jsonPath.contains(".")) {
                String[] pathParts = jsonPath.substring(2).split("\\.");
                Object rootObject = stepResults.get(pathParts[0]);

                // Parse root object to JSON node if it's a JSON-like string
                ObjectNode currentNode = JsonUtils.parseToJsonNode(rootObject);

                if (currentNode == null) {
                    logger.warn("Root object not found or is not a valid JSON object for path: {}", jsonPath);
                    return;
                }

                ObjectNode parentNode = currentNode;
                for (int i = 1; i < pathParts.length - 1; i++) {
                    JsonNode childNode = parentNode.get(pathParts[i]);

                    if (childNode == null || !childNode.isObject()) {
                        logger.warn("Path not found or non-object encountered at '{}'", pathParts[i]);
                        return;
                    }
                    parentNode = (ObjectNode) childNode;
                }

                // Remove the final key in the path
                parentNode.remove(pathParts[pathParts.length - 1]);

                // Update the modified structure in stepResults
                if (rootObject instanceof String) {
                    // Convert updated JSON node back to a string
                    stepResults.put(pathParts[0], mapper.writeValueAsString(currentNode));
                } else {
                    // Directly update as an object if it was originally non-string
                    stepResults.put(pathParts[0], mapper.treeToValue(currentNode, Object.class));
                }

            } else {
                // Direct key removal for top-level keys
                stepResults.remove(jsonPath);
            }
        } catch (Exception e) {
            logger.error("Error deleting value for '{}': {}", jsonPath, e.getMessage());
        }
    }

}
