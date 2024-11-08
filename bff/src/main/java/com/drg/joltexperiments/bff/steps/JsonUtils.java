package com.drg.joltexperiments.bff.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Option;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;

public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("(\\w+)\\[(\\d+)]");


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

    //todo: add testcases to get array operation as first item, second item, last item and different operations


    public static Optional<Object> extractJsonPathValue(String sourcePath, Map<String, Object> stepResults) {
        if (stepResults.containsKey(sourcePath)){
            return Optional.ofNullable(stepResults.get(sourcePath));
        }else {
            return extractJsonPathValueUsingJsonPath(sourcePath,stepResults);
        }
    }

    public static Optional<List<JsonNode>> extractJsonPathAsList(String jsonPath, Map<String, Object> context) {
        Optional<Object> listOpt = JsonUtils.extractJsonPathValue(jsonPath, context);

        if (listOpt.isPresent()) {
            Object result = listOpt.get();
            try {
                if (result instanceof String) {
                    // Parse the JSON string back into a list
                    return Optional.of(mapper.readValue((String) result, new TypeReference<List<JsonNode>>() {}));
                } else if (result instanceof List<?>) {
                    List<JsonNode> jsonNodeList = new ArrayList<>();
                    for (Object item : (List<?>) result) {
                        jsonNodeList.add(mapper.valueToTree(item));
                    }
                    return Optional.of(jsonNodeList);
                } else {
                    logger.warn("Expected a list at JSON path '{}', but found type: {}", jsonPath, result.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Failed to parse JSON path '{}' to List: {}", jsonPath, e.getMessage());
            }
        } else {
            logger.warn("No value found at JSON path '{}'", jsonPath);
        }
        return Optional.empty();
    }

    public static Optional<Object> extractJsonPathValueUsingJsonPath(String sourcePath, Map<String, Object> stepResults ) {
        try {
            // Ensure JSONPath starts with "$." if it doesn't already
            String jsonPath = sourcePath.startsWith("$.") ? sourcePath : "$." + sourcePath;
            String rootKey;

            // Extract the root key, considering if it includes aggregation
            String[] pathComponents = jsonPath.replaceFirst("^\\$\\.", "").split("\\.");

            if (pathComponents[0].contains("(")) {
                rootKey = pathComponents[1].replaceAll("\\[.*?\\]", "");  // Skip aggregation function part
            } else {
                rootKey = pathComponents[0].replaceAll("\\[.*?\\]", "");  // Handle array notation
            }
            // Get the root object from stepResults
            Object rootObject = stepResults.get(rootKey);
            if (rootObject == null) {
                return Optional.empty();
            }

            // If rootObject is a primitive (or simple) value, return it directly
            if (!(rootObject instanceof Map || rootObject instanceof List || rootObject.toString().contains("{") || rootObject.toString().contains("["))) {
                return Optional.of(rootObject);
            }

            Map<String, Object> wrappedRoot = new HashMap<>();
            wrappedRoot.put(rootKey, initializeJsonNode(rootObject,mapper));
            rootObject = wrappedRoot;

            // Convert rootObject to JsonNode if it's structured (object/array) for further processing
            JsonNode rootNode = initializeJsonNode(rootObject, mapper);
            if (rootNode == null) {
                return Optional.empty();
            }

            Configuration configuration = Configuration.defaultConfiguration();

            // Use JSONPath to extract the value
            Object document = configuration.jsonProvider().parse(rootNode.toString());
            Object value = JsonPath.read(document, jsonPath);

            if (value instanceof Map || value instanceof List) {
                // Convert complex types (objects, arrays) to JSON string
                return Optional.of(mapper.writeValueAsString(value));
            } else {
                // Directly return simple types
                return Optional.ofNullable(value);
            }
        } catch (JsonPathException e) {
            logger.error("Error extracting JSON path value for path '{}': {}", sourcePath, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static Optional<Object> extractJsonPathValueUsingNativeCode(String sourcePath, Map<String, Object> stepResults, ObjectMapper mapper) {
        try {
            String[] pathParts = sourcePath.substring(2).split("\\.");

            // Handle potential array index in the root part
            JsonNode currentNode = getRootNodeWithOptionalIndex(pathParts[0], stepResults, mapper);
            if (currentNode == null) {
                return Optional.empty();
            }

            for (int i = 1; i < pathParts.length && currentNode != null; i++) {
                String part = pathParts[i];

                // Handle special operations without traversing further
                if ("length".equals(part)) {
                    return currentNode.isArray() ? Optional.of(currentNode.size()) : Optional.empty();
                }
                if ("isEmpty".equals(part)) {
                    return Optional.of(currentNode.isArray() ? currentNode.size() == 0 : currentNode.asText().isEmpty());
                }
                if ("isNotEmpty".equals(part)) {
                    return Optional.of(currentNode.isArray() ? currentNode.size() > 0 : !currentNode.asText().isEmpty());
                }
                if ("sum".equals(part) || "avg".equals(part) || "min".equals(part) || "max".equals(part)) {
                    return handleNumericAggregation(part, currentNode);
                }
                if ("toUpperCase".equals(part) && currentNode.isTextual()) {
                    return Optional.of(currentNode.asText().toUpperCase());
                }
                if ("toLowerCase".equals(part) && currentNode.isTextual()) {
                    return Optional.of(currentNode.asText().toLowerCase());
                }
                if ("first".equals(part) && currentNode.isArray()) {
                    return Optional.ofNullable(currentNode.get(0));
                }
                if ("last".equals(part) && currentNode.isArray()) {
                    return Optional.ofNullable(currentNode.get(currentNode.size() - 1));
                }

                // Handle array indexing and standard field traversal
                Matcher matcher = ARRAY_INDEX_PATTERN.matcher(part);
                if (matcher.matches()) {
                    String arrayName = matcher.group(1);
                    int index = Integer.parseInt(matcher.group(2));

                    currentNode = currentNode.get(arrayName);
                    if (currentNode != null && currentNode.isArray() && currentNode.size() > index) {
                        currentNode = currentNode.get(index);
                    } else {
                        return Optional.empty();
                    }
                } else {
                    currentNode = currentNode.get(part);
                }
            }

            return Optional.ofNullable(currentNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static JsonNode getRootNodeWithOptionalIndex(String rootPath, Map<String, Object> stepResults, ObjectMapper mapper) {
        Matcher matcher = ARRAY_INDEX_PATTERN.matcher(rootPath);
        Object rootObject;
        JsonNode rootNode = null;

        if (matcher.matches()) {
            // Handle root part with array index, e.g., "searchCustomer[0]"
            String arrayName = matcher.group(1);
            int index = Integer.parseInt(matcher.group(2));
            rootObject = stepResults.get(arrayName);

            rootNode = initializeJsonNode(rootObject, mapper);
            if (rootNode != null && rootNode.isArray() && rootNode.size() > index) {
                rootNode = rootNode.get(index);
            } else {
                return null;
            }
        } else {
            // Handle root part without array index
            rootObject = stepResults.get(rootPath);
            rootNode = initializeJsonNode(rootObject, mapper);
        }

        return rootNode;
    }

    private static JsonNode initializeJsonNode(Object rootObject, ObjectMapper mapper) {
        try {
            if (rootObject instanceof String) {
                String rootString = (String) rootObject;
                if (rootString.trim().startsWith("{") || rootString.trim().startsWith("[")) {
                    return mapper.readTree(rootString);
                } else {
                    return mapper.convertValue(rootString, JsonNode.class);
                }
            } else if (rootObject != null) {
                return mapper.convertValue(rootObject, JsonNode.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Optional<Object> handleNumericAggregation(String part, JsonNode currentNode) {
        if (!currentNode.isArray()) return Optional.empty();

        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        int count = 0;

        for (JsonNode element : currentNode) {
            if (element.isNumber()) {
                double value = element.asDouble();
                sum += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
                count++;
            }
        }

        switch (part) {
            case "sum":
                return Optional.of(sum);
            case "avg":
                return count > 0 ? Optional.of(sum / count) : Optional.empty();
            case "min":
                return count > 0 ? Optional.of(min) : Optional.empty();
            case "max":
                return count > 0 ? Optional.of(max) : Optional.empty();
            default:
                return Optional.empty();
        }
    }
}



