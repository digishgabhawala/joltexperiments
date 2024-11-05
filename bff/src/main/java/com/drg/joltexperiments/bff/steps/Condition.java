package com.drg.joltexperiments.bff.steps;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.Objects;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Condition {

    private String key;           // The key to evaluate the condition on
    private Operator operator;      // Operator (e.g., EQUALS, NOT_EQUALS, GREATER_THAN, etc.)
    private String value;         // Value to compare with

    private LogicalOperator logicalOperator; // AND/OR/NOT logic for the condition

    private String ifStep;
    private String elseStep;
    public enum LogicalOperator {
        AND, OR, NOT
    }

    public enum Operator {
        EQUALS, NOT_EQUALS, CONTAINS, NOT_CONTAINS,
        GREATER_THAN, LESS_THAN, EXISTS, NOT_EXISTS,
        MATCHES_REGEX, IN, NOT_IN
    }


    public Condition() {
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LogicalOperator getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public String getIfStep() {
        return ifStep;
    }

    public void setIfStep(String ifStep) {
        this.ifStep = ifStep;
    }

    public String getElseStep() {
        return elseStep;
    }

    public void setElseStep(String elseStep) {
        this.elseStep = elseStep;
    }

    public Condition(String key, Operator operator, String value, LogicalOperator logicalOperator, String ifStep, String elseStep) {
        this.key = key;
        this.operator = operator;
        this.value = value;
        this.logicalOperator = logicalOperator;
        this.ifStep = ifStep;
        this.elseStep = elseStep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return Objects.equals(key, condition.key) && operator == condition.operator && Objects.equals(value, condition.value) && logicalOperator == condition.logicalOperator && Objects.equals(ifStep, condition.ifStep) && Objects.equals(elseStep, condition.elseStep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, operator, value, logicalOperator, ifStep, elseStep);
    }
}
