package com.drg.joltexperiments.bff.steps.Operate;

import com.drg.joltexperiments.bff.ServiceConfigEntity;
import com.drg.joltexperiments.bff.Step;
import com.drg.joltexperiments.bff.steps.JsonUtils;
import com.drg.joltexperiments.bff.steps.StepInteface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Scope("prototype")
public class OperateStep implements StepInteface {

    private static final Logger logger = LoggerFactory.getLogger(OperateStep.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String execute(HttpHeaders headers, String body, Step step, Map<String, Object> stepResults, ServiceConfigEntity config) {
        Operate operation = step.getOperate(); // Assume Operate is set on Step
        if (operation == null) {
            logger.error("No operation found in step configuration.");
            return "No operation to execute.";
        }
        if(operation.getOperator().equalsIgnoreCase("JSONPATH_RESOLVE_INDEX")){
            return resolveJsonPathIndex(operation,stepResults);
        } else if (operation.getOperator().equalsIgnoreCase("ADDTOJSONLIST")) {
            return addToJsonList(operation,stepResults);
        }

        Optional<Object> op1ValueOpt = JsonUtils.extractJsonPathValue(operation.getOp1(), stepResults);
        Optional<Object> op2ValueOpt = JsonUtils.extractJsonPathValue(operation.getOp2(), stepResults);

        if (op1ValueOpt.isEmpty() || op2ValueOpt.isEmpty()) {
            logger.error("One or both operands could not be resolved: op1={}, op2={}", operation.getOp1(), operation.getOp2());
            return "Invalid operands.";
        }

        Object op1Value = parseOperand((String) op1ValueOpt.get(), operation.getOp1Type());
        Object op2Value = parseOperand((String) op2ValueOpt.get(), operation.getOp2Type());
        logger.debug("Executing operation: {} {} {}", op1Value, operation.getOperator(), op2Value);

        try {
            Object result = executeOperation(op1Value, operation.getOperator(), op2Value);
            JsonUtils.updateValueInStepResults(operation.getResult(),result.toString(),stepResults);
            logger.debug("Operation executed. Result stored in '{}': {}", operation.getResult(), result);
            return "Operation executed successfully.";
        } catch (UnsupportedOperationException e) {
            logger.error("Failed to execute operation: {}", e.getMessage());
            return "Operation execution failed.";
        }
    }

    public String addToJsonList(Operate operation, Map<String, Object> stepResults) {
        Optional<List<JsonNode>> op1ValueOpt = JsonUtils.extractJsonPathAsList(operation.getOp1(), stepResults);
        Optional<Object> op2ValueOpt = JsonUtils.extractJsonPathValue(operation.getOp2(), stepResults);
        try {
            JsonNode node = mapper.readTree((String) op2ValueOpt.get());
            List<JsonNode> op1Value = op1ValueOpt.get();
                    op1Value.add(node);
            String result = mapper.writeValueAsString(op1Value);
            JsonUtils.updateValueInStepResults(operation.getResult(),result.toString(),stepResults);
            return "";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public String resolveJsonPathIndex(Operate operation, Map<String, Object> stepResults) {
        Optional<Object> op2ValueOpt = JsonUtils.extractJsonPathValue(operation.getOp2(), stepResults);
        if (op2ValueOpt.isEmpty()) {
            logger.error("operands could not be resolved: op2={}",  operation.getOp2());
            return "Invalid operands.";
        }
        Object op2Value = parseOperand((String) op2ValueOpt.get(), operation.getOp2Type());
        String newOp1Value = operation.getOp1().replace(operation.getOp2(),op2Value.toString());
        Optional<Object> op1ValueOpt = JsonUtils.extractJsonPathValue(newOp1Value, stepResults);
        if (op1ValueOpt.isEmpty()) {
            logger.error("operands could not be resolved: op1={}",  operation.getOp1());
            return "Invalid operands.";
        }
        JsonUtils.updateValueInStepResults(operation.getResult(),op1ValueOpt.get().toString(),stepResults);
        return "";
    }


    private Object parseOperand(String value, Operate.OperandType type) {
        switch (type) {
            case INTEGER:
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new UnsupportedOperationException("Invalid integer operand: " + value);
                }
            case DOUBLE:
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    throw new UnsupportedOperationException("Invalid double operand: " + value);
                }
            case STRING:
                return value;
            default:
                throw new UnsupportedOperationException("Unsupported operand type: " + type);
        }
    }

    private Object executeOperation(Object op1, String operator, Object op2) {
        switch (operator.toLowerCase()) {
            case "add":
                return add(op1, op2);
            case "subtract":
                return subtract(op1, op2);
            case "multiply":
                return multiply(op1, op2);
            case "divide":
                return divide(op1, op2);
            case "modulus":
                return modulus(op1, op2);
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
    }

    private Object add(Object op1, Object op2) {
        if (op1 instanceof Integer && op2 instanceof Integer) {
            return (Integer) op1 + (Integer) op2;
        } else if (op1 instanceof Double || op2 instanceof Double) {
            return convertToDouble(op1) + convertToDouble(op2);
        } else if (op1 instanceof String || op2 instanceof String) {
            return op1.toString() + op2.toString();
        }
        throw new UnsupportedOperationException("Unsupported operand types for addition");
    }

    private Object subtract(Object op1, Object op2) {
        if (op1 instanceof Integer && op2 instanceof Integer) {
            return (Integer) op1 - (Integer) op2;
        } else if (op1 instanceof Double || op2 instanceof Double) {
            return convertToDouble(op1) - convertToDouble(op2);
        }
        throw new UnsupportedOperationException("Unsupported operand types for subtraction");
    }

    private Object multiply(Object op1, Object op2) {
        if (op1 instanceof Integer && op2 instanceof Integer) {
            return (Integer) op1 * (Integer) op2;
        } else if (op1 instanceof Double || op2 instanceof Double) {
            return convertToDouble(op1) * convertToDouble(op2);
        }
        throw new UnsupportedOperationException("Unsupported operand types for multiplication");
    }

    private Object divide(Object op1, Object op2) {
        if (op1 instanceof Integer && op2 instanceof Integer && (Integer) op2 != 0) {
            return (Integer) op1 / (Integer) op2;
        } else if ((op1 instanceof Double || op2 instanceof Double) && convertToDouble(op2) != 0) {
            return convertToDouble(op1) / convertToDouble(op2);
        }
        throw new UnsupportedOperationException("Unsupported operand types or division by zero");
    }

    private Object modulus(Object op1, Object op2) {
        if (op1 instanceof Integer && op2 instanceof Integer) {
            return (Integer) op1 % (Integer) op2;
        } else if (op1 instanceof Double || op2 instanceof Double) {
            return convertToDouble(op1) % convertToDouble(op2);
        }
        throw new UnsupportedOperationException("Unsupported operand types for modulus operation");
    }

    private double convertToDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        throw new UnsupportedOperationException("Non-numeric operand: " + obj);
    }
}
