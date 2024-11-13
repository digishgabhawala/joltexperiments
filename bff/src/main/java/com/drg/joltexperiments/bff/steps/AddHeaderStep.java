package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@Scope("prototype")
public class AddHeaderStep implements StepInteface {

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, ServiceConfigEntity config) {
        Map<String, String> mappings = step.getMappings();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode bodyNode = mapper.createObjectNode();

        if (mappings != null) {
            mappings.forEach((sourcePath, targetKey) -> {
                JsonUtils.extractJsonPathValue(sourcePath, stepResults)
                        .ifPresent(value -> bodyNode.putPOJO(targetKey, value));
            });
        }
        stepResults.put(step.getName(), bodyNode.toString());
        return "";
    }
}
