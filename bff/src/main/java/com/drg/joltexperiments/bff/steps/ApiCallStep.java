package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class ApiCallStep implements StepInteface {

    private static final Logger logger = LoggerFactory.getLogger(ApiCallStep.class);
    public ApiCallStep() {
    }

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        String url = buildUrlWithVariables(step.getServiceUrl(), stepResults);
        logger.info("Making API call to: {}", url);

        String requestBody = (String) stepResults.getOrDefault(step.getBody(), body); // Use configured body if available

        validateRequestSchema(step.getRequestSchema(), requestBody);

        HttpHeaders finalHeaders = buildHeaders(headers, stepResults, step.getHeaders());

        String response = executeRestTemplateRequest(finalHeaders, requestBody, url, step.getMethod());
        JsonUtils.validateSchema(step.getResponseSchema(), response, "Response");
        stepResults.put(step.getName(), response);
        return response;
    }

    private void validateRequestSchema(String requestSchema, String requestBody) {
        if (requestSchema != null && !requestSchema.isEmpty()) {
            try {
                JsonUtils.validateSchema(requestSchema, requestBody, "Request");
                logger.info("Request body is valid according to the schema.");
            } catch (Exception e) {
                logger.error("Request validation failed: {}", e.getMessage());
                throw new IllegalArgumentException("Invalid request body: " + e.getMessage(), e);
            }
        }
    }

    private String buildUrlWithVariables(String urlTemplate, Map<String, Object> stepResults) {
        for (Map.Entry<String, Object> entry : stepResults.entrySet()) {
            urlTemplate = urlTemplate.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return urlTemplate;
    }

    private HttpHeaders buildHeaders( HttpHeaders originalHeaders, Map<String, Object> stepResults, String headersKey) {
        HttpHeaders finalHeaders = new HttpHeaders();
        // Copy default headers
        originalHeaders.forEach((key, values) -> {
            if (!key.equalsIgnoreCase("Content-Length") &&
                    !key.equalsIgnoreCase("Host") &&
                    !key.equalsIgnoreCase("Transfer-Encoding")) {
                finalHeaders.put(key, values); // Copy only necessary headers
            }
        });

        // Add custom headers from stepResults if headersKey is specified
        if (headersKey != null && stepResults.containsKey(headersKey)) {

            try {
                JsonNode headersNode = JsonUtils.parseToJsonNode(stepResults.get(headersKey));
                headersNode.fields().forEachRemaining(header -> finalHeaders.add(header.getKey(), header.getValue().asText()));
            } catch (Exception e) {
                logger.error("Error parsing custom headers: {}", e.getMessage());
            }

        }
        return finalHeaders;
    }

    private String executeRestTemplateRequest(HttpHeaders headers, String body, String url, String method) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Execute the request with the selected HTTP method
            ResponseEntity<String> response = "POST".equalsIgnoreCase(method) ?
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class) :
                    restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            // Validate response against the provided schema

            return response.getBody();
        } catch (Exception e) {
            logger.error("Error occurred during RestTemplate request: {}", e.getMessage());
            throw e;
        }
    }


}
