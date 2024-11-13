package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import com.drg.joltexperiments.bff.steps.Operate.OperateStep;
import com.drg.joltexperiments.bff.steps.Tokens.DetokenizeVariablesStep;
import com.drg.joltexperiments.bff.steps.Tokens.TokenizeVariablesStep;
import com.drg.joltexperiments.bff.steps.condition.ConditionStep;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class StepFactory {

    private final ObjectProvider<ApiCallStep> apiCallStepProvider;
    private final ObjectProvider<CombineResponsesStep> combineResponsesStepProvider;
    private final ObjectProvider<BuildBodyStep> buildBodyStepProvider;
    private final ObjectProvider<RenameVariablesStep> renameVariablesStepProvider;
    private final ObjectProvider<AddVariablesStep> addVariablesStepProvider;
    private final ObjectProvider<DeleteVariablesStep> deleteVariablesStepProvider;
    private final ObjectProvider<AddHeaderStep> addHeaderStepProvider;
    private final ObjectProvider<CompositeStep> compositeStepProvider;
    private final ObjectProvider<ConditionStep> conditionStepProvider;
    private final ObjectProvider<FilterListStep> filterListStepProvider;
    private final ObjectProvider<OperateStep> operateStepProvider;
    private final ObjectProvider<TokenizeVariablesStep> tokenizeVariablesStepProvider;
    private final ObjectProvider<DetokenizeVariablesStep> detokenizeVariablesStepProvider;

    public StepFactory(
            ObjectProvider<ApiCallStep> apiCallStepProvider,
            ObjectProvider<CombineResponsesStep> combineResponsesStepProvider,
            ObjectProvider<BuildBodyStep> buildBodyStepProvider,
            ObjectProvider<RenameVariablesStep> renameVariablesStepProvider,
            ObjectProvider<AddVariablesStep> addVariablesStepProvider,
            ObjectProvider<DeleteVariablesStep> deleteVariablesStepProvider,
            ObjectProvider<AddHeaderStep> addHeaderStepProvider,
            ObjectProvider<CompositeStep> compositeStepProvider,
            ObjectProvider<ConditionStep> conditionStepProvider,
            ObjectProvider<FilterListStep> filterListStepProvider,
            ObjectProvider<OperateStep> operateStepProvider,
            ObjectProvider<TokenizeVariablesStep> tokenizeVariablesStepProvider,
            ObjectProvider<DetokenizeVariablesStep> detokenizeVariablesStepProvider
    ) {
        this.apiCallStepProvider = apiCallStepProvider;
        this.combineResponsesStepProvider = combineResponsesStepProvider;
        this.buildBodyStepProvider = buildBodyStepProvider;
        this.renameVariablesStepProvider = renameVariablesStepProvider;
        this.addVariablesStepProvider = addVariablesStepProvider;
        this.deleteVariablesStepProvider = deleteVariablesStepProvider;
        this.addHeaderStepProvider = addHeaderStepProvider;
        this.compositeStepProvider = compositeStepProvider;
        this.conditionStepProvider = conditionStepProvider;
        this.filterListStepProvider = filterListStepProvider;
        this.operateStepProvider = operateStepProvider;
        this.tokenizeVariablesStepProvider = tokenizeVariablesStepProvider;
        this.detokenizeVariablesStepProvider = detokenizeVariablesStepProvider;
    }

    public StepInteface createStep(Step step) {
        switch (step.getType().toLowerCase()) {
            case "apicall":
                return apiCallStepProvider.getObject();
            case "combineresponses":
                return combineResponsesStepProvider.getObject();
            case "buildbody":
                return buildBodyStepProvider.getObject();
            case "renamevariables":
                return renameVariablesStepProvider.getObject();
            case "addvariables":
                return addVariablesStepProvider.getObject();
            case "deletevariables":
                return deleteVariablesStepProvider.getObject();
            case "addheaders":
                return addHeaderStepProvider.getObject();
            case "composite":
                return compositeStepProvider.getObject();
            case "condition":
                return conditionStepProvider.getObject();
            case "filterlist":
                return filterListStepProvider.getObject();
            case "operate":
                return operateStepProvider.getObject();
            case "tokenize":
                return tokenizeVariablesStepProvider.getObject();
            case "detokenize":
                return detokenizeVariablesStepProvider.getObject();
            default:
                throw new IllegalArgumentException("Unknown step type: " + step.getType());
        }
    }
}
