package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import org.springframework.stereotype.Component;

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
            case "addheaders":
                return new AddHeaderStep();
            case "composite":
                return new CompositeStep();
            case "condition":
                return new ConditionStep();
            default:
                throw new IllegalArgumentException("Unknown step type: " + step.getType());
        }
    }
}
