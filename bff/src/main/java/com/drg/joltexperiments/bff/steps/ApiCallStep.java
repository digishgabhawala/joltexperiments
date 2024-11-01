package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.Set;

public class ApiCallStep implements StepInteface {

    private static final Logger logger = LoggerFactory.getLogger(ApiCallStep.class);
//    //todo: make headers configuraable
private final ObjectMapper mapper = new ObjectMapper();

    public ApiCallStep() {
    }

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        String url = buildUrlWithVariables(step.getServiceUrl(), stepResults);
        logger.info("Making API call to: {}", url);

        String requestBody = (String) stepResults.getOrDefault(step.getBody(), body); // Use configured body if available

        HttpHeaders finalHeaders = new HttpHeaders();

        addCustomHeaders(finalHeaders, headers, stepResults, step.getHeaders());

        String response = executeRestTemplateRequest(finalHeaders, requestBody, url, step.getMethod(), step.getResponseSchema());
        stepResults.put(step.getName(), response);
        return response;
    }

    private String buildUrlWithVariables(String urlTemplate, Map<String, Object> stepResults) {
        for (Map.Entry<String, Object> entry : stepResults.entrySet()) {
            urlTemplate = urlTemplate.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return urlTemplate;
    }

    private void addCustomHeaders(HttpHeaders finalHeaders, HttpHeaders originalHeaders, Map<String, Object> stepResults, String headersKey) {
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
    }



    //todo: make headers configuraable
    private String executeRestTemplateRequest(HttpHeaders headers, String body, String url, String method, String responseSchema) {
        RestTemplate restTemplate = new RestTemplate();

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
}
