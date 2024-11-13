package com.drg.joltexperiments.bff.steps;


import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompositeStep implements StepInteface {

    private final StepFactory stepFactory;

    @Autowired
    public CompositeStep(StepFactory stepFactory) {
        this.stepFactory = stepFactory;
    }
    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, final ServiceConfigEntity config) {
        List<String> subStepNames = step.getItemsList();
        String result = "";
        List<String> subStepNamesList = new ArrayList<>();
        Map<String, Object> compositeResults = new HashMap<>(); // Store composite results here

        for (String subStepName : subStepNames) {

            Step currentSubStep = config.getSteps().stream()
                    .filter(stepItem -> stepItem.getName().equals(subStepName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Sub-step not found: " + subStepName));

            logger.debug("Executing sub-step: [{}] within composite step: [{}]", subStepName, step.getName());


            StepInteface stepInstance = stepFactory.createStep(currentSubStep);
            result = stepInstance.execute(headers, body, currentSubStep, stepResults,config);
            compositeResults.put(currentSubStep.getName(), stepResults.get(currentSubStep.getName()));

        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValueAsString(compositeResults);
            String compositeResultJson = mapper.writeValueAsString(compositeResults);
            stepResults.put(step.getName(), compositeResultJson);
            //remove any child substep details as those are added in composite one
            for(String subStepName : subStepNamesList){
                stepResults.remove(subStepName);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return result;
    }
}
