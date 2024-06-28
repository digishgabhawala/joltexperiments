package com.drg.joltexperiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final String TOKEN_SERVICE_URL = "http://localhost:9001";
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Request URI: {}", request.getURI());

        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            String token = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0).substring(7);
            log.info("Token: {}", token);

            return webClientBuilder.build()
                    .get()
                    .uri(TOKEN_SERVICE_URL + "/token/validate?token=" + token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (Boolean.TRUE.equals(isValid)) {
                            return webClientBuilder.build()
                                    .get()
                                    .uri(TOKEN_SERVICE_URL + "/token/authId?token=" + token)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .flatMap(authId -> {
                                        ServerHttpRequest modifiedRequest = request.mutate()
                                                .header("X-Auth-Id", authId)
                                                .build();
                                        log.info("Authenticated with Auth ID: {}", authId);
                                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authId, null, null);
                                        SecurityContext securityContext = new SecurityContextImpl(authentication);

                                        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

//                                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                                    });
                        } else {
                            log.error("Invalid Token");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();

//                            return Mono.error(new RuntimeException("Invalid Token"));
                        }
                    });
        }

        log.info("No Authorization header found, proceeding without token validation");
        return chain.filter(exchange);
    }
}
