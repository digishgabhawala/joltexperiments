package com.drg.joltexperiments.bff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bff/api/service-configs")
public class ServiceConfigController {

    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createServiceConfig(@RequestBody ServiceConfigEntity serviceConfigEntity) {
        if (serviceConfigEntity.getPath() == null || serviceConfigEntity.getPath().isEmpty() ||
                serviceConfigEntity.getMethod() == null || serviceConfigEntity.getMethod().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Both 'path' and 'method' fields must be provided."
            ));
        }

        serviceConfigRepository.save(serviceConfigEntity);
        return getResponse("created", serviceConfigEntity.getPath(), serviceConfigEntity.getMethod());
    }

    private static ResponseEntity<Map<String, Object>> getResponse(String operation, String path, String method) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Service config " + operation + " successfully.",
                "path", path,
                "method", method
        ));
    }

    @GetMapping("/{path}/{method}")
    public ResponseEntity<ServiceConfigEntity> getServiceConfig(@PathVariable String path, @PathVariable String method) {
        Optional<ServiceConfigEntity> serviceConfigEntity = serviceConfigRepository.findByPathAndMethod(path, method);
        return serviceConfigEntity.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{path}/{method}")
    public ResponseEntity<Map<String, Object>> updateServiceConfig(
            @PathVariable String path,
            @PathVariable String method,
            @RequestBody ServiceConfigEntity serviceConfigEntity) {

        Optional<ServiceConfigEntity> existingConfig = serviceConfigRepository.findByPathAndMethod(path, method);

        if (existingConfig.isPresent()) {
            ServiceConfigEntity entity = existingConfig.get();
            entity.setServiceUrl(serviceConfigEntity.getServiceUrl());
            entity.setApiDocsUrl(serviceConfigEntity.getApiDocsUrl());
            entity.setRequestSchema(serviceConfigEntity.getRequestSchema());
            entity.setResponseSchema(serviceConfigEntity.getResponseSchema());

            // Update the steps if included in the request
            if (serviceConfigEntity.getSteps() != null) {
                entity.setSteps(serviceConfigEntity.getSteps());
            }

            serviceConfigRepository.save(entity);
            return getResponse("updated", path, method);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{path}/{method}")
    public ResponseEntity<Map<String, Object>> deleteServiceConfig(@PathVariable String path, @PathVariable String method) {
        Optional<ServiceConfigEntity> serviceConfigEntity = serviceConfigRepository.findByPathAndMethod(path, method);

        if (serviceConfigEntity.isPresent()) {
            serviceConfigRepository.delete(serviceConfigEntity.get());
            return getResponse("deleted", path, method);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ServiceConfigEntity>> getAllServiceConfigs() {
        List<ServiceConfigEntity> entities = serviceConfigRepository.findAll();
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetServiceConfigData() {
        try {
            serviceConfigRepository.deleteAll();
            return getResponse("reset", "all", "ALL");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    @GetMapping("/reset/{path}/{method}")
    public ResponseEntity<Map<String, Object>> resetServiceConfigDataForPath(@PathVariable String path, @PathVariable String method) {
        return deleteServiceConfig(path,method);
    }
}