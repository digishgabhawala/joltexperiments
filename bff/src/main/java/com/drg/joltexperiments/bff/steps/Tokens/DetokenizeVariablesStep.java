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
import java.util.Optional;

@Component
public class DetokenizeVariablesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(DetokenizeVariablesStep.class);

    @Autowired
    private final TokenStorageService tokenStorageService;

    @Autowired
    public DetokenizeVariablesStep(TokenStorageService tokenStorageService) {
        this.tokenStorageService = tokenStorageService;
    }

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config) {
        Map<String, String> detokenizeMappings = step.getMappings();
        ObjectMapper mapper = new ObjectMapper();

        if (detokenizeMappings != null) {
            detokenizeMappings.forEach((targetPath, tokenKey) -> {
                Object token = stepResults.get(tokenKey);

                if (token != null) {
                    Optional<Object> originalData = tokenStorageService.retrieveTokenData(token.toString());

                    if (originalData.isPresent()) {
                        logger.debug("Original data found for token {} = {}", tokenKey, originalData);
                        stepResults.put(targetPath,originalData.get());
                    } else {
                        logger.warn("No data found for token {}", tokenKey);
                    }
                } else {
                    logger.warn("Token {} not found in stepResults", tokenKey);
                }
            });
        }
        return "";
    }
}
