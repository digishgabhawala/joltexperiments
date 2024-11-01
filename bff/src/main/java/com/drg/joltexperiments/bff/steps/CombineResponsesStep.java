package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombineResponsesStep implements StepInteface{
    private static final Logger logger = LoggerFactory.getLogger(ApiCallStep.class);

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        try {
            // Get the list of response keys to combine from the Step object
            List<String> responseKeys = step.getItemsList();

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
