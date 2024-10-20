package com.drg.joltexperiments.bff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bff/api/service-configs")
public class ServiceConfigController {

    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createServiceConfig(@RequestBody ServiceConfigEntity serviceConfigEntity) {
        if (serviceConfigEntity.getPath() == null || serviceConfigEntity.getPath().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "The 'path' field must be provided."
            ));
        }

        serviceConfigRepository.save(serviceConfigEntity);
        return getResponse("created", serviceConfigEntity.getPath());
    }

    private static ResponseEntity<Map<String, Object>> getResponse(String operation, String path) {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Service config " + operation + " successfully.",
                "path", path
        ));
    }


    @GetMapping("/{path}")
    public ResponseEntity<ServiceConfigEntity> getServiceConfig(@PathVariable String path) {
        return serviceConfigRepository.findById(path)
                .map(entity -> ResponseEntity.ok(entity))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{path}")
    public ResponseEntity<Map<String,Object>> updateServiceConfig( @RequestBody ServiceConfigEntity serviceConfigEntity) {
        return serviceConfigRepository.findById(serviceConfigEntity.getPath())
                .map(entity -> {
                    entity.setServiceUrl(serviceConfigEntity.getServiceUrl());
                    entity.setApiDocsUrl(serviceConfigEntity.getApiDocsUrl());
                    entity.setSchema(serviceConfigEntity.getSchema());
                    serviceConfigRepository.save(entity);
                    return getResponse( "updated", serviceConfigEntity.getPath());
//                    return ResponseEntity.ok("Service config updated successfully for path: " + path);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{path}")
    public ResponseEntity<Map<String,Object>> deleteServiceConfig(@PathVariable String path) {
        if (serviceConfigRepository.existsById(path)) {
            serviceConfigRepository.deleteById(path);
            return getResponse("deleted", path);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ServiceConfigEntity>> searchServiceConfigs() {
        List<ServiceConfigEntity> entities = serviceConfigRepository.findAll();
        return ResponseEntity.ok(entities);
    }
}
