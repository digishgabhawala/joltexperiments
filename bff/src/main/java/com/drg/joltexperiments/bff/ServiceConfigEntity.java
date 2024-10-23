package com.drg.joltexperiments.bff;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "service_config", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"path", "method"})
})
public class ServiceConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Primary key

    private String path;   // Path of the API
    private String method; // HTTP method (GET, POST, etc.)

    private String serviceUrl;  // URL of the primary API, if applicable
    private String apiDocsUrl;  // Link to API documentation

    @Lob
    @Column(length = 10000)
    private String requestSchema;  // New field for request schema

    @Lob
    @Column(length = 10000)
    private String responseSchema; // Field for response schema

    private String finalResponseKey;  // To store which step’s output to return as the final response

    // Embed the steps directly into the table
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "service_config_steps", joinColumns = @JoinColumn(name = "service_config_id"))
    private List<Step> steps;  // List of steps

    // Constructors, Getters, and Setters

    public ServiceConfigEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getFinalResponseKey() {
        return finalResponseKey;
    }

    public void setFinalResponseKey(String finalResponseKey) {
        this.finalResponseKey = finalResponseKey;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
