package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Map;

public class RenameVariablesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(ApiCallStep.class);


    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults) {
        Map<String, String> renameMappings = step.getMappings();
        ObjectMapper mapper = new ObjectMapper();

        if (renameMappings != null) {
            renameMappings.forEach((sourcePath, targetKey) -> {
                extractJsonPathValue(sourcePath, stepResults, mapper)
                        .ifPresent(value -> stepResults.put(targetKey, value));
            });
        }
        return "";
    }
}
