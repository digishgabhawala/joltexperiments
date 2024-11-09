package com.drg.joltexperiments.bff.steps;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OperateStepTest {


    @Test
    void resolveDynamicJsonPath() {
        Operate operate = new Operate();
        operate.setOp1("$.fetchCustomers[$.loopProcessingIndex]");
        operate.setOp2("$.loopProcessingIndex");
        operate.setOp2Type(Operate.OperandType.INTEGER);
        operate.setResult("currentCustomer");

        Map<String, Object> mappings = new HashMap<>();

        String loopProcessingIndex = "0";
        mappings.put("loopProcessingIndex",loopProcessingIndex);
        String fetchCustomers = "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]";
        mappings.put("fetchCustomers",fetchCustomers);
        mappings.put("finalResult","[]");


        new OperateStep().resolveJsonPathIndex(operate,mappings);
        System.out.println(mappings.get("currentCustomer"));
        loopProcessingIndex = "1";
        mappings.put("loopProcessingIndex",loopProcessingIndex);
        new OperateStep().resolveJsonPathIndex(operate,mappings);


        System.out.println(mappings.get("currentCustomer"));

        Operate addToJsonList = new Operate();
        addToJsonList.setResult("finalResult");
        addToJsonList.setOp1("$.finalResult");
        addToJsonList.setOp2("$.currentCustomer");
        addToJsonList.setOp1Type(Operate.OperandType.LIST);
        addToJsonList.setOp2Type(Operate.OperandType.JSON);
        addToJsonList.setOperator("ADDTOJSONLIST");

        new OperateStep().addToJsonList(addToJsonList,mappings);
        System.out.println(mappings.get("finalResult"));


    }

    @Test
    void resolveJsonPathIndex() {

    }
}