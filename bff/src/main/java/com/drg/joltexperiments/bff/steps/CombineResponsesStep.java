package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombineResponsesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(CombineResponsesStep.class);

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config) {
        String response = getCombineResponseStr(step,stepResults);
        stepResults.put(step.getName(),response);
        return response;
    }

    private String getCombineResponseStr(Step step, Map<String, Object> stepResults){
        try {
            // Get the list of response keys to combine from the Step object
            List<String> responseKeys = step.getItemsList();

            // Initialize ObjectMapper for JSON handling
            ObjectMapper mapper = new ObjectMapper();

            // Check if a specific combine strategy is set
            String combineStrategy = step.getCombineStrategy();

            if ("selectFirstNonNull".equalsIgnoreCase(combineStrategy)) {
                // Iterate through response keys in order to find the first non-null entry
                for (String key : responseKeys) {
                    if (stepResults.containsKey(key) && stepResults.get(key) != null) {
                        String response = (String) stepResults.get(key);
                        JsonNode responseJson = mapper.readTree(response);
                        return mapper.writeValueAsString(responseJson); // Return the first non-null response
                    }
                }
                // If no non-null result found, log and return null or an empty JSON
                logger.warn("No non-null response found for keys: {}", responseKeys);
                return "{}"; // Returning empty JSON if no non-null response is found
            } else {
                // Default combine strategy - merging all responses into a single result
                Map<String, Object> combinedResult = new HashMap<>();
                for (String key : responseKeys) {
                    if (stepResults.containsKey(key)) {
                        String response = (String) stepResults.get(key);
                        JsonNode responseJson = mapper.readTree(response);
                        combinedResult.put(key, responseJson); // Use the key for the combined result
                    } else {
                        logger.warn("Response key '{}' not found in stepResults", key);
                    }
                }
                return mapper.writeValueAsString(combinedResult); // Return merged result
            }
        } catch (Exception e) {
            logger.error("Error combining responses: {}", e.getMessage());
            throw new RuntimeException("Failed to combine responses", e);
        }
    }
}
