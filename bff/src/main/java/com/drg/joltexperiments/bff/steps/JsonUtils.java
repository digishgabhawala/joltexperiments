package com.drg.joltexperiments.bff.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

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

    public static void validateSchema(String schemaJson, String data, String validationType) {
        if (schemaJson == null || schemaJson.isEmpty()) {
            logger.warn("{} schema not found, skipping validation.", validationType);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode schemaNode = mapper.readTree(schemaJson);
            JsonSchema schema = JsonSchemaFactory.getInstance().getSchema(schemaNode);
            JsonNode dataNode = mapper.readTree(data);

            Set<ValidationMessage> validationMessages = schema.validate(dataNode);
            if (!validationMessages.isEmpty()) {
                throw new IllegalArgumentException(validationType + " validation failed: " +
                        String.join("\n", validationMessages.stream()
                                .map(ValidationMessage::getMessage)
                                .toList()));
            }
            logger.info("{} is valid according to the schema.", validationType);
        } catch (Exception e) {
            logger.error("{} validation error: {}", validationType, e.getMessage());
            throw new IllegalArgumentException(validationType + " validation error: " + e.getMessage(), e);
        }
    }
}
