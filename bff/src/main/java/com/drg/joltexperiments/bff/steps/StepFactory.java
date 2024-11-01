package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StepFactory {

    public StepInteface createStep(Step step) {

        switch (step.getType().toLowerCase()) {
            case "apicall":
                return new ApiCallStep();
            case "combineresponses":
                return new CombineResponsesStep();
            case "buildbody":
                return new BuildBodyStep();
            case "renamevariables":
                return new RenameVariablesStep();
            case "addvariables":
                return new AddVariablesStep();
            case "deletevariables":
                return new DeleteVariablesStep();
            default:
                throw new IllegalArgumentException("Unknown step type: " + step.getType());
        }
    }
}
