package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class ConditionStep implements StepInteface {

    private static final Logger logger = LoggerFactory.getLogger(ConditionStep.class);


    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, ServiceConfigEntity config) {
        Condition condition = step.getCondition(); // Assume Condition is set on Step
        if (condition == null || evaluateCondition(condition, stepResults)) {
            if (condition != null && condition.getIfStep() != null) {
                step.setNextStep(condition.getIfStep());
                logger.debug("Condition met. Moving to ifStep: {}", condition.getIfStep());
                return "Condition met; proceeding with ifStep.";
            }
        } else if (condition != null && condition.getElseStep() != null) {
            step.setNextStep(condition.getElseStep());
            logger.debug("Condition not met. Moving to elseStep: {}", condition.getElseStep());
            return "Condition not met; proceeding with elseStep.";
        }
        logger.debug("No condition to evaluate, or next step not set.");
        return "No condition or step to proceed.";
    }

    private boolean evaluateCondition(Condition condition, Map<String, Object> stepResults) {

        Optional<Object> valueToCompareOpt = JsonUtils.extractJsonPathValue(condition.getKey(), stepResults);
        Condition.Operator operator = condition.getOperator();
        String expectedValue = condition.getValue();

        // If the value is not found and the operator is NOT_EXISTS, return true
        if (valueToCompareOpt.isEmpty()) {
            logger.debug("Condition key '{}' not found in step results.", condition.getKey());
            return operator == Condition.Operator.NOT_EXISTS;
        }

        // Get the value and evaluate the condition
        Object valueToCompare = valueToCompareOpt.get();
        boolean result = evaluateSingleCondition(valueToCompare, operator, expectedValue);
        logger.debug("Evaluating condition '{}' with operator '{}' and value '{}': Result = {}",
                condition.getKey(), operator, expectedValue, result);
        return result;
    }

    private boolean evaluateSingleCondition(Object valueToCompare, Condition.Operator operator, String expectedValue) {
        if (valueToCompare == null) return false;

        switch (operator) {
            case EQUALS:
                return valueToCompare.toString().equals(expectedValue);
            case NOT_EQUALS:
                return !valueToCompare.toString().equals(expectedValue);
            case GREATER_THAN:
                if (valueToCompare instanceof Number && isNumeric(expectedValue)) {
                    return ((Number) valueToCompare).doubleValue() > Double.parseDouble(expectedValue);
                }
                break;
            case LESS_THAN:
                if (valueToCompare instanceof Number && isNumeric(expectedValue)) {
                    return ((Number) valueToCompare).doubleValue() < Double.parseDouble(expectedValue);
                }
                break;
            case CONTAINS:
                return valueToCompare.toString().contains(expectedValue);
            case NOT_CONTAINS:
                return !valueToCompare.toString().contains(expectedValue);
            case EXISTS:
                return valueToCompare != null;
            case NOT_EXISTS:
                return valueToCompare == null;
            case MATCHES_REGEX:
                return valueToCompare.toString().matches(expectedValue);
            case IN:
                String[] expectedValues = expectedValue.split(","); // assuming a comma-separated list
                for (String val : expectedValues) {
                    if (val.trim().equals(valueToCompare.toString())) {
                        return true;
                    }
                }
                return false;
            case NOT_IN:
                expectedValues = expectedValue.split(","); // assuming a comma-separated list
                for (String val : expectedValues) {
                    if (val.trim().equals(valueToCompare.toString())) {
                        return false;
                    }
                }
                return true;
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
        return false;
    }

    private boolean applyLogicalOperator(boolean result, boolean subResult, Condition.LogicalOperator logicalOp) {
        switch (logicalOp) {
            case AND:
                return result && subResult;
            case OR:
                return result || subResult;
            case NOT:
                return !subResult;
            default:
                throw new UnsupportedOperationException("Unknown logical operator: " + logicalOp);
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
