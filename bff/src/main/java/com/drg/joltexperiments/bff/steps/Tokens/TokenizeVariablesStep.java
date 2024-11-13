package com.drg.joltexperiments.bff.steps.Tokens;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.drg.joltexperiments.bff.steps.JsonUtils;
import com.drg.joltexperiments.bff.steps.StepInteface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class TokenizeVariablesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(TokenizeVariablesStep.class);

    @Autowired
    private final TokenStorageService tokenStorageService;

    @Autowired
    public TokenizeVariablesStep(TokenStorageService tokenStorageService) {
        this.tokenStorageService = tokenStorageService;
    }

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config) {
        Map<String, String> tokenizeMappings = step.getMappings();
        ObjectMapper mapper = new ObjectMapper();

        if (tokenizeMappings != null) {
            tokenizeMappings.forEach((targetKey, sourcePath) -> {
                Object data = JsonUtils.extractJsonPathValue(sourcePath, stepResults).orElse(null);

                if (data != null) {
                    String token = UUID.randomUUID().toString();
                    tokenStorageService.storeToken(token, data); // Store token with data in a persistent store

                    logger.debug("Token generated for {} = {}", targetKey, token);
                    stepResults.put(targetKey, token);
                } else {
                    logger.warn("No data found at path {}", sourcePath);
                }
            });
        }
        return "";
    }
}
