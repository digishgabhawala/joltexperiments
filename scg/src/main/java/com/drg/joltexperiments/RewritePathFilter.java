package com.drg.joltexperiments;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RewritePathFilter extends AbstractGatewayFilterFactory<RewritePathFilter.Config> {

    public static class Config {
        private String pattern;
        private String replacement;

        // Getters and setters

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getReplacement() {
            return replacement;
        }

        public void setReplacement(String replacement) {
            this.replacement = replacement;
        }
    }

    public RewritePathFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String originalPath = request.getURI().getPath();

            String rewrittenPath = originalPath.replaceAll(config.getPattern(), config.getReplacement());

            ServerHttpRequest modifiedRequest = request.mutate()
                    .path(rewrittenPath)
                    .build();

            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

            return chain.filter(modifiedExchange);
        };
    }
}
