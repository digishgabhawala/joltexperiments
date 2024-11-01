package com.drg.joltexperiments.bff.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode parseToJsonNode(Object rootObject) {
        ObjectNode currentNode = mapper.createObjectNode();

        if (rootObject instanceof String) {
            String rootString = (String) rootObject;
            try {
                if (rootString.trim().startsWith("{") || rootString.trim().startsWith("[")) {
                    currentNode = (ObjectNode) mapper.readTree(rootString);
                } else {
                    currentNode.put("value", rootString);
                }
            } catch (Exception e) {
                logger.error("Error parsing JSON string '{}': {}", rootString, e.getMessage());
            }
        } else if (rootObject != null) {
            currentNode = mapper.convertValue(rootObject, ObjectNode.class);
        }
        return currentNode;
    }


}
