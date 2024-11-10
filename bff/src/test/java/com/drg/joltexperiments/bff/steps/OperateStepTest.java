package com.drg.joltexperiments.bff.steps;

import com.drg.joltexperiments.bff.Step;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OperateStepTest {


    @Test
    void testStringAddition() {



        Map<String, Object> mappings = new HashMap<>();

        mappings.put("loopProcessingIndex","0");
        mappings.put("nameSuffix","_new");
        String fetchCustomers = "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]";
        mappings.put("fetchCustomers",fetchCustomers);
        mappings.put("finalResult","[]");

        Operate operate = new Operate();
        operate.setOp1("$.fetchCustomers[$.loopProcessingIndex]");
        operate.setOp2("$.loopProcessingIndex");
        operate.setOp2Type(Operate.OperandType.INTEGER);
        operate.setResult("currentCustomer");
        new OperateStep().resolveJsonPathIndex(operate,mappings);

        Operate editCustomerName = new Operate();
        editCustomerName.setOp1("$.currentCustomer.firstName");
        editCustomerName.setOp2("$.nameSuffix");
        editCustomerName.setOp1Type(Operate.OperandType.STRING);
        editCustomerName.setOp2Type(Operate.OperandType.STRING);
        editCustomerName.setOperator("add");
        editCustomerName.setResult("$.currentCustomer.firstName");

        Step step = new Step();
        step.setOperate(editCustomerName);
        new OperateStep().execute(null,null,step,mappings,null);
        System.out.println(JsonUtils.extractJsonPathValue("$.currentCustomer.firstName",mappings));
    }


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
    void testUpdate() {
        Map<String, Object> mappings = new HashMap<>();

        mappings.put("loopProcessingIndex","0");
        mappings.put("nameSuffix","_new");
        String fetchCustomers = "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]";
        mappings.put("fetchCustomers",fetchCustomers);
        mappings.put("finalResult","[]");

        Operate operate = new Operate();
        operate.setOp1("$.fetchCustomers[$.loopProcessingIndex]");
        operate.setOp2("$.loopProcessingIndex");
        operate.setOp2Type(Operate.OperandType.INTEGER);
        operate.setResult("currentCustomer");
        new OperateStep().resolveJsonPathIndex(operate,mappings);


        String newValue = "newVal";

        JsonUtils.updateValueInStepResults("$.currentCustomer.firstName",newValue,mappings);
        JsonUtils.updateValueInStepResults("nameSuffix",newValue,mappings);
        System.out.println(mappings.get("currentCustomer"));
    }
}