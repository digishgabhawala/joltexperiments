package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.drg.joltexperiments.bff.steps.condition.Condition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Scope("prototype")
public class FilterListStep implements StepInteface {

    private static final Logger logger = LoggerFactory.getLogger(FilterListStep.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, ServiceConfigEntity config) {
        Condition condition = step.getCondition();

        if (condition == null) {
            logger.warn("FilterListStep requires a condition to be specified.");
            return "No condition found; filter list cannot be applied.";
        }


        Optional<List<JsonNode>> masterListOpt = JsonUtils.extractJsonPathAsList(step.getInputKey(), stepResults);
        if (masterListOpt.isEmpty()) {
            return "Expected list not found at specified JSON path.";
        }

        List<JsonNode> originalList = masterListOpt.get();

        Optional<List<JsonNode>> conditionValueListOpt = JsonUtils.extractJsonPathAsList(condition.getValue(),stepResults);
        if(conditionValueListOpt.isEmpty()){
            return "Condition values list is empty";
        }
        List<JsonNode> expectedValueList = conditionValueListOpt.get();

        List<JsonNode> filteredList = new ArrayList<>();
        for (JsonNode itemNode : originalList) {
            boolean matchesCondition = evaluateCondition(condition, itemNode,null,expectedValueList);

            if (matchesCondition) {
                filteredList.add(itemNode);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String filteredListJson;
        try {
            filteredListJson = objectMapper.writeValueAsString(filteredList);
        } catch (Exception e) {
            logger.error("Error converting filtered list to JSON string", e);
            return "Error converting filtered list to JSON string.";
        }

        stepResults.put(step.getName(), filteredListJson);
        logger.debug("Filtered list size: {}", filteredList.size());
        return "Filter list operation completed successfully.";
    }

    private boolean evaluateCondition(Condition condition, JsonNode itemNode,String expectedValue,List<JsonNode> expectedValueList) {
        // Use JSONPath to directly extract the value from the item node based on the condition key.
        Object valueToCompare;
        try {
            valueToCompare = JsonPath.parse(itemNode.toString()).read(condition.getKey());
        } catch (Exception e) {
            logger.warn("Could not find key '{}' in item node. Exception: {}", condition.getKey(), e.getMessage());
            return condition.getOperator() == Condition.Operator.NOT_EXISTS;
        }

        // Evaluate condition based on operator and expected value
        return evaluateSingleCondition(valueToCompare, condition.getOperator(),expectedValue,expectedValueList);
    }

    private boolean evaluateSingleCondition(Object valueToCompare, Condition.Operator operator, String expectedValue, List<JsonNode> expectedValueList) {
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
                // Use expectedValueList if available, otherwise use expectedValue
                if (expectedValueList != null && !expectedValueList.isEmpty()) {
                    for (JsonNode val : expectedValueList) {
                        if (valueToCompare.toString().contains(val.asText())) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    return valueToCompare.toString().contains(expectedValue);
                }
            case NOT_CONTAINS:
                if (expectedValueList != null && !expectedValueList.isEmpty()) {
                    for (JsonNode val : expectedValueList) {
                        if (valueToCompare.toString().contains(val.asText())) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return !valueToCompare.toString().contains(expectedValue);
                }
            case EXISTS:
                return valueToCompare != null;
            case NOT_EXISTS:
                return valueToCompare == null;
            case MATCHES_REGEX:
                return valueToCompare.toString().matches(expectedValue);
            case IN:
                // Use expectedValueList if available
                if (expectedValueList != null && !expectedValueList.isEmpty()) {
                    for (JsonNode val : expectedValueList) {
                        if (val.asText().equals(valueToCompare.toString())) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    // Fallback to comma-separated expectedValue if expectedValueList is not provided
                    String[] expectedValues = expectedValue.split(",");
                    for (String val : expectedValues) {
                        if (val.trim().equals(valueToCompare.toString())) {
                            return true;
                        }
                    }
                    return false;
                }
            case NOT_IN:
                if (expectedValueList != null && !expectedValueList.isEmpty()) {
                    for (JsonNode val : expectedValueList) {
                        if (val.asText().equals(valueToCompare.toString())) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    String[] expectedValues = expectedValue.split(",");
                    for (String val : expectedValues) {
                        if (val.trim().equals(valueToCompare.toString())) {
                            return false;
                        }
                    }
                    return true;
                }
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
        return false;
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
