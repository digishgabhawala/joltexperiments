package com.drg.joltexperiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    private static final String AUTHENTICATION_SERVICE_URL = "http://localhost:9002";
    private static final String TOKEN_SERVICE_URL = "http://localhost:9001";
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody AuthRequest authRequest) {
        log.debug("Sending login request to {}", AUTHENTICATION_SERVICE_URL + "/auth/login");
        log.debug("AuthRequest: {}", authRequest);

        return webClientBuilder.build()
                .post()
                .uri(AUTHENTICATION_SERVICE_URL + "/auth/login")
                .bodyValue(authRequest)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .doOnNext(authResponse -> log.debug("Received AuthResponse: {}", authResponse))
                .flatMap(authResponse -> webClientBuilder.build()
                        .post()
                        .uri(TOKEN_SERVICE_URL + "/token/generate")
                        .bodyValue(new TokenRequest(authResponse.getAuthId().toString()))
                        .retrieve()
                        .bodyToMono(String.class)
                        .map(token -> ResponseEntity.ok().body("{\"token\": \"" + token + "\"}"))
                        .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"" + error.getMessage() + "\"}")))
                )
                .doOnError(error -> log.error("Error during login:", error));
    }
}

