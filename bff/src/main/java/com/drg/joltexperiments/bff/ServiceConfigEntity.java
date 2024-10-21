package com.drg.joltexperiments.bff;

import jakarta.persistence.*;

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
    private String serviceUrl;
    private String apiDocsUrl;
    @Lob // This annotation tells JPA to use a larger data type for this field
    @Column(length = 10000) // Optionally specify a larger length
    private String requestSchema;  // New field for request schema
    @Lob // This annotation tells JPA to use a larger data type for this field
    @Column(length = 10000) // Optionally specify a larger length
    private String responseSchema; // Existing field or renamed for clarity


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
}
