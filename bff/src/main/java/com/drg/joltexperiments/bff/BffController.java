package com.drg.joltexperiments.bff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class BffController {

    private static final Logger logger = LoggerFactory.getLogger(BffController.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Map<String, String> serviceUrlMap;

    @GetMapping("/bff/**")
    public Mono<String> getGeneric(@RequestHeader HttpHeaders headers, ServerHttpRequest request) {
        String path = extractPathFromRequest(request);
        String url = getServiceUrlFromPath(path);

        String queryParams = request.getURI().getQuery();
        if (queryParams != null && !queryParams.isEmpty()) {
            url += "?" + queryParams;
        }

        logger.info("GET request URL: {}", url);

        return webClientBuilder.build()
                .get()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping("/bff/**")
    public Mono<String> postGeneric(@RequestHeader HttpHeaders headers, @RequestBody String body, ServerHttpRequest request) {
        String path = extractPathFromRequest(request);
        String url = getServiceUrlFromPath(path);

        String queryParams = request.getURI().getQuery();
        if (queryParams != null && !queryParams.isEmpty()) {
            url += "?" + queryParams;
        }

        logger.info("POST request URL: {}", url);

        return webClientBuilder.build()
                .post()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    private String extractPathFromRequest(ServerHttpRequest request) {
        String requestPath = request.getPath().pathWithinApplication().value();
        return requestPath.substring("/bff/".length());
    }

    private String getServiceUrlFromPath(String path) {
        for (Map.Entry<String, String> entry : serviceUrlMap.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue() + path.substring(entry.getKey().length());
            }
        }
        throw new IllegalArgumentException("No matching service URL found for path: " + path);
    }
}
