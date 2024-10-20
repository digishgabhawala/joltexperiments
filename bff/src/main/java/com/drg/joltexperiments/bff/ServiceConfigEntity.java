package com.drg.joltexperiments.bff;

import jakarta.persistence.*;

@Entity
public class ServiceConfigEntity {
    @Id
    private String path; // Path as the primary key
    private String serviceUrl;
    private String apiDocsUrl;
    @Lob // This annotation tells JPA to use a larger data type for this field
    @Column(length = 10000) // Optionally specify a larger length
    private String schema;

    // Constructors, Getters, and Setters

    public ServiceConfigEntity() {
    }

    public ServiceConfigEntity(String path, String serviceUrl, String apiDocsUrl, String schema) {
        this.path = path;
        this.serviceUrl = serviceUrl;
        this.apiDocsUrl = apiDocsUrl;
        this.schema = schema;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
