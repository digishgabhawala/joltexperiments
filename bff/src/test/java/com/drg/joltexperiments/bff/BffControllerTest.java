package com.drg.joltexperiments.bff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(BffController.class)
@Import({TestConfig.class, WebClientConfig.class})
public class BffControllerTest {

    @Test
    public void testValidResponse() {
        String validResponse = getMockValidResponse();
        String schemaJson = getMockSchemaJson();

        assertDoesNotThrow(() -> validateResponseSchema(validResponse, schemaJson));
    }

    @Test
    public void testInvalidResponse() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponse(), getMockSchemaJson()));
    }

    @Test
    public void testInvalidResponse2() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponse2(), getMockSchemaJson()));
    }

    @Test
    public void testInvalidResponse3() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponse3(), getMockSchemaJson()));
    }

    @Test
    public void testMissingMandatoryField() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponseMissingMandatoryField(), getMockSchemaJson()));
    }

    @Test
    public void testInvalidEmailFormat() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponseInvalidEmailFormat(), getMockSchemaJson()));
    }

    @Test
    public void testAdditionalField() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponseAdditionalField(), getMockSchemaJson()));
    }

    @Test
    public void testValidResponseWithArraySchema() {
        String validResponse = getMockValidResponseArray();
        String schemaJson = getMockSchemaJson2();

        assertDoesNotThrow(() -> validateResponseSchema(validResponse, schemaJson));
    }

    @Test
    public void testInvalidResponseWithArraySchema() {
        assertThrows(IllegalArgumentException.class, () -> validateResponseSchema(getMockInvalidResponseArray(), getMockSchemaJson2()));
    }

    private void validateResponseSchema(String response, String schemaJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode schemaNode = mapper.readTree(schemaJson);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance();
            JsonSchema schema = factory.getSchema(schemaNode);

            JsonNode responseNode = mapper.readTree(response);
            Set<ValidationMessage> validationMessages = schema.validate(responseNode);

            if (!validationMessages.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder("Response validation failed:");
                for (ValidationMessage message : validationMessages) {
                    errorMessages.append("\n").append(message.getMessage());
                }
                System.out.println(errorMessages.toString());
                throw new IllegalArgumentException(errorMessages.toString());
            }

            System.out.println("Response is valid according to the schema.");
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON provided.", e);
        }
    }

    private String getMockSchemaJson() {
        return "{\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"id\": {\n" +
                "      \"type\": \"integer\",\n" +
                "      \"format\": \"int64\"\n" +
                "    },\n" +
                "    \"firstName\": {\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    \"lastName\": {\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    \"email\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"format\": \"email\"\n" +
                "    },\n" +
                "    \"creationDate\": {\n" +
                "      \"type\": \"string\",\n" +
                "      \"format\": \"date-time\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\"id\"],\n" +
                "  \"additionalProperties\": false\n" +
                "}";
    }

    private String getMockSchemaJson2() {
        return "{\n" +
                "  \"type\": \"array\",\n" +
                "  \"items\": {\n" +
                "    \"$ref\": \"#/components/schemas/Customer\"\n" +
                "  },\n" +
                "  \"components\": {\n" +
                "    \"schemas\": {\n" +
                "      \"Customer\": {\n" +
                "        \"type\": \"object\",\n" +
                "        \"properties\": {\n" +
                "          \"id\": {\n" +
                "            \"type\": \"integer\",\n" +
                "            \"format\": \"int64\"\n" +
                "          },\n" +
                "          \"firstName\": {\n" +
                "            \"type\": \"string\"\n" +
                "          },\n" +
                "          \"lastName\": {\n" +
                "            \"type\": \"string\"\n" +
                "          },\n" +
                "          \"email\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"format\": \"email\"\n" +
                "          },\n" +
                "          \"creationDate\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"format\": \"date-time\"\n" +
                "          }\n" +
                "        },\n" +
                "        \"required\": [\"id\"],\n" +
                "        \"additionalProperties\": false\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private String getMockValidResponse() {
        return "{\n" +
                "  \"id\": 1,\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"creationDate\": \"2023-05-01T12:00:00Z\"\n" +
                "}";
    }

    private String getMockInvalidResponse() {
        return "{\n" +
                "  \"id\": 1,\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lassstName\": \"Doe\",\n" +
                "}";
    }

    private String getMockInvalidResponse2() {
        return "{\n" +
                "  \"id\": \"one\",\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"creationDate\": \"2023-05-01T12:00:00Z\"\n" +
                "}";
    }

    private String getMockInvalidResponse3() {
        return "{\n" +
                "  \"id\": 1,\n" +
                "  \"firstName\": { \"John\" },\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john.doe@example.com\"\n" +
                "}";
    }

    private String getMockInvalidResponseMissingMandatoryField() {
        return "{\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john.doe@example.com\"\n" +
                "}";
    }

    private String getMockInvalidResponseInvalidEmailFormat() {
        return "{\n" +
                "  \"id\": 1,\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john.doe.example.com\",\n" +
                "  \"creationDate\": \"2023-05-01T12:00:00Z\"\n" +
                "}";
    }

    private String getMockInvalidResponseAdditionalField() {
        return "{\n" +
                "  \"id\": 1,\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"creationDate\": \"2023-05-01T12:00:00Z\",\n" +
                "  \"extraField\": \"extra\"\n" +
                "}";
    }

    private String getMockValidResponseArray() {
        return "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"lastName\": \"Doe\",\n" +
                "    \"email\": \"john.doe@example.com\",\n" +
                "    \"creationDate\": \"2023-05-01T12:00:00Z\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"firstName\": \"Jane\",\n" +
                "    \"lastName\": \"Smith\",\n" +
                "    \"email\": \"jane.smith@example.com\",\n" +
                "    \"creationDate\": \"2023-06-01T12:00:00Z\"\n" +
                "  }\n" +
                "]";
    }

    private String getMockInvalidResponseArray() {
        return "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"lastName\": \"Doe\",\n" +
                "    \"email\": \"john.doe@example.com\",\n" +
                "    \"creationDate\": \"2023-05-01T12:00:00Z\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"firstName\": \"Jane\",\n" +
                "    \"lastName\": \"Smith\",\n" +
                "    \"email\": \"jane.smith@example.com\",\n" +
                "    \"creationDate\": \"2023-06-01T12:00:00Z\",\n" +
                "    \"invalidField\": \"invalid\"\n" +
                "  }\n" +
                "]";
    }
}
