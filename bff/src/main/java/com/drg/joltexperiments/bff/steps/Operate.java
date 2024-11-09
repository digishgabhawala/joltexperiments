package com.drg.joltexperiments.bff.steps;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import java.util.Objects;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Operate {

    private String op1;                       // First operand (can be a variable or a constant)
    @Enumerated(EnumType.STRING)
    private OperandType op1Type;              // Type of the first operand (e.g., INTEGER, DOUBLE, STRING)
    private String operator;                  // Operator (e.g., add, subtract, multiply, divide, etc.)
    private String op2;                       // Second operand (can be a variable or a constant)
    @Enumerated(EnumType.STRING)
    private OperandType op2Type;              // Type of the second operand (e.g., INTEGER, DOUBLE, STRING)
    private String result;                    // Variable to store the result of the operation

    public Operate() {
    }

    public Operate(String op1, OperandType op1Type, String operator, String op2, OperandType op2Type, String result) {
        this.op1 = op1;
        this.op1Type = op1Type;
        this.operator = operator;
        this.op2 = op2;
        this.op2Type = op2Type;
        this.result = result;
    }

    public String getOp1() {
        return op1;
    }

    public void setOp1(String op1) {
        this.op1 = op1;
    }

    public OperandType getOp1Type() {
        return op1Type;
    }

    public void setOp1Type(OperandType op1Type) {
        this.op1Type = op1Type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOp2() {
        return op2;
    }

    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public OperandType getOp2Type() {
        return op2Type;
    }

    public void setOp2Type(OperandType op2Type) {
        this.op2Type = op2Type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operate operate = (Operate) o;
        return Objects.equals(op1, operate.op1) &&
                op1Type == operate.op1Type &&
                Objects.equals(operator, operate.operator) &&
                Objects.equals(op2, operate.op2) &&
                op2Type == operate.op2Type &&
                Objects.equals(result, operate.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op1, op1Type, operator, op2, op2Type, result);
    }

    public enum OperandType {
        INTEGER,
        DOUBLE,
        STRING
    }
}
