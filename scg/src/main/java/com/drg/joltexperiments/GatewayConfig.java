package com.drg.joltexperiments;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, RewritePathFilter rewritePathFilter) {
        RewritePathFilter.Config customerConfig = new RewritePathFilter.Config();
        customerConfig.setPattern("/customers(?<remaining>.*)");
        customerConfig.setReplacement("/api/customers${remaining}");

        RewritePathFilter.Config cardsConfig = new RewritePathFilter.Config();
        cardsConfig.setPattern("/cards(?<remaining>.*)");
        cardsConfig.setReplacement("/api/cards${remaining}");

        RewritePathFilter.Config accountsConfig = new RewritePathFilter.Config();
        accountsConfig.setPattern("/accounts(?<remaining>.*)");
        accountsConfig.setReplacement("/api/accounts${remaining}");

        RewritePathFilter.Config kycConfig = new RewritePathFilter.Config();
        kycConfig.setPattern("/kyc(?<remaining>.*)");
        kycConfig.setReplacement("/api/kyc${remaining}");


        return builder.routes()
                .route("customer-service", r -> r.path("/customers/**")
                        .filters(f -> f.filter(rewritePathFilter.apply(customerConfig)))
                        .uri("http://localhost:9003"))
                .route("cards-service", r -> r.path("/cards/**")
                        .filters(f -> f.filter(rewritePathFilter.apply(cardsConfig)))
                        .uri("http://localhost:9004"))
                .route("acounts-service", r -> r.path("/accounts/**")
                        .filters(f -> f.filter(rewritePathFilter.apply(accountsConfig)))
                        .uri("http://localhost:9005"))
                .route("kyc-service", r -> r.path("/kyc/**")
                        .filters(f -> f.filter(rewritePathFilter.apply(kycConfig)))
                        .uri("http://localhost:9006"))
                .route("bff-service", r -> r.path("/bff/**")
                        .uri("http://localhost:9007"))
                .build();
    }
}
