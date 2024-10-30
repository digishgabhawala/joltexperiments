package com.drg.joltexperiments.bff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.util.*;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Step {

    private String name;  // Step name, like "getCustomer" or "transformData"
    private String type;  // Type of step: "apiCall", "joltTransform", etc.
    private String method;  // HTTP method (GET, POST, etc.) for API steps

    private String body;  // HTTP body for API steps

    private String serviceUrl;  // URL of the API to call (for API steps)
    private String apiDocsUrl;  // Link to API documentation (optional)

    private String inputKey;  // Key to identify input from the previous step
    private String outputKey;  // Key to store the output of this step

    private String path;

    @Lob
    @Column(length = 10000)
    private String transformSpec;  // Jolt transformation spec for transformations, if applicable

    @Lob
    @Column(length = 10000)
    private String responseKey;  // Key to store the response from an API call

    @Lob
    @Column(length = 10000)
    private String requestSchema;  // New field for request schema

    @Lob
    @Column(length = 10000)
    private String responseSchema; // Field for response schema

    @Lob
    @Column(length = 10000)
    private String renameMappingsJson;  // Store JSON representation of the map

    private String combineStrategy;  // For combineResponses step

    @Lob
    @Column(length = 10000)
    private String combineResponsesJson;  // List of responses for combine step

    // Constructors, Getters, Setters, and hashCode/equals for Embeddable class

    @Transient
    private List<String> combineResponses;

    public Step() {
    }

    public Step(String name, String type, String method, String serviceUrl, String apiDocsUrl,
                String inputKey, String outputKey, String path, String transformSpec,
                String responseKey, String requestSchema, String responseSchema, Map<String, String> renameMappings,
                String combineStrategy, String body, List<String> combineResponses) {
        this.body = body;
        this.name = name;
        this.type = type;
        this.method = method;
        this.serviceUrl = serviceUrl;
        this.apiDocsUrl = apiDocsUrl;
        this.inputKey = inputKey;
        this.outputKey = outputKey;
        this.path = path;
        this.transformSpec = transformSpec;
        this.responseKey = responseKey;
        this.requestSchema = requestSchema;
        this.responseSchema = responseSchema;
        this.setRenameMappings(renameMappings);
        this.combineStrategy = combineStrategy;
        this.setCombineResponses(combineResponses);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getApiDocsUrl() {
        return apiDocsUrl;
    }

    public void setApiDocsUrl(String apiDocsUrl) {
        this.apiDocsUrl = apiDocsUrl;
    }

    public String getInputKey() {
        return inputKey;
    }

    public void setInputKey(String inputKey) {
        this.inputKey = inputKey;
    }

    public String getOutputKey() {
        return outputKey;
    }

    public void setOutputKey(String outputKey) {
        this.outputKey = outputKey;
    }

    public String getTransformSpec() {
        return transformSpec;
    }

    public void setTransformSpec(String transformSpec) {
        this.transformSpec = transformSpec;
    }

    public String getResponseKey() {
        return responseKey;
    }

    public void setResponseKey(String responseKey) {
        this.responseKey = responseKey;
    }

    public String getRequestSchema() {
        return requestSchema;
    }

    public void setRequestSchema(String requestSchema) {
        this.requestSchema = requestSchema;
    }

    public String getResponseSchema() {
        return responseSchema;
    }

    public void setResponseSchema(String responseSchema) {
        this.responseSchema = responseSchema;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getCombineResponses() {
        if (combineResponsesJson == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(combineResponsesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>(); // Return an empty list on error
        }
    }

    // Setter for combineResponses
    public void setCombineResponses(List<String> combineResponses) {
        try {
            this.combineResponses = combineResponses; // Store the list in the transient field
            this.combineResponsesJson = objectMapper.writeValueAsString(combineResponses); // Serialize to JSON
        } catch (JsonProcessingException e) {
            this.combineResponsesJson = null; // Set to null if serialization fails
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(name, step.name) && Objects.equals(type, step.type)
                && Objects.equals(method, step.method) && Objects.equals(serviceUrl, step.serviceUrl)
                && Objects.equals(apiDocsUrl, step.apiDocsUrl) && Objects.equals(inputKey, step.inputKey)
                && Objects.equals(outputKey, step.outputKey) && Objects.equals(path, step.path)
                && Objects.equals(transformSpec, step.transformSpec) && Objects.equals(responseKey, step.responseKey)
                && Objects.equals(requestSchema, step.requestSchema) && Objects.equals(responseSchema, step.responseSchema)
                && Objects.equals(renameMappingsJson, step.renameMappingsJson) && Objects.equals(combineStrategy, step.combineStrategy)
                && Objects.equals(body, step.body) && Objects.equals(combineResponsesJson, step.combineResponsesJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, method, serviceUrl, apiDocsUrl, inputKey, outputKey, path,
                transformSpec, responseKey, requestSchema, responseSchema, renameMappingsJson, combineStrategy, body, combineResponsesJson);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> getRenameMappings() {
        if (renameMappingsJson == null) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(renameMappingsJson, Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }

    public void setRenameMappings(Map<String, String> renameMappings) {
        try {
            this.renameMappingsJson = objectMapper.writeValueAsString(renameMappings);
        } catch (JsonProcessingException e) {
            this.renameMappingsJson = null;
        }
    }

    public String getCombineStrategy() {
        return combineStrategy;
    }

    public void setCombineStrategy(String combineStrategy) {
        this.combineStrategy = combineStrategy;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
