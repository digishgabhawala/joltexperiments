package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Map;

public class RenameVariablesStep implements StepInteface {
    private static final Logger logger = LoggerFactory.getLogger(ApiCallStep.class);


    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config) {
        Map<String, String> renameMappings = step.getMappings();
        ObjectMapper mapper = new ObjectMapper();

        if (renameMappings != null) {
            renameMappings.forEach((sourcePath, targetKey) -> {
                Object data = JsonUtils.extractJsonPathValue(sourcePath, stepResults, mapper).get();
                logger.debug("data found = {} for key {}",data,targetKey);
                stepResults.put(targetKey, data);
            });
        }
        return "";
    }
}
