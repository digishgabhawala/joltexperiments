* After adding all serviceConfig, it looks like below:
  *
[
{
"id": 31,
"path": "customers",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"responseSchema": "{\n  \"type\": \"array\",\n  \"items\": {\n    \"type\": \"object\",\n    \"properties\": {\n      \"id\": {\n        \"type\": \"integer\",\n        \"format\": \"int64\"\n      },\n      \"firstName\": {\n        \"type\": \"string\"\n      },\n      \"lastName\": {\n        \"type\": \"string\"\n      },\n      \"email\": {\n        \"type\": \"string\",\n        \"format\": \"email\"\n      }\n    },\n    \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n  }\n}",
"steps": [
{
"name": "fetchCustomers",
"type": "apiCall",
"serviceUrl": "http://localhost:9003/api/customers",
"responseSchema": "{\n  \"type\": \"array\",\n  \"items\": {\n    \"type\": \"object\",\n    \"properties\": {\n      \"id\": {\n        \"type\": \"integer\",\n        \"format\": \"int64\"\n      },\n      \"firstName\": {\n        \"type\": \"string\"\n      },\n      \"lastName\": {\n        \"type\": \"string\"\n      },\n      \"email\": {\n        \"type\": \"string\",\n        \"format\": \"email\"\n      }\n    },\n    \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n  }\n}"
}
]
},
{
"id": 32,
"path": "customers/{id}",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers/{id}",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"finalResponseKey": "getCustomer",
"steps": [
{
"name": "getCustomer",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers/{id}",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"outputKey": "customerData",
"path": "customers/{id}",
"responseKey": "customerData",
"responseSchema": "{\n  \"type\": \"object\",\n  \"properties\": {\n    \"id\": {\n      \"type\": \"integer\",\n      \"format\": \"int64\"\n    },\n    \"firstName\": {\n      \"type\": \"string\"\n    },\n    \"lastName\": {\n      \"type\": \"string\"\n    },\n    \"email\": {\n      \"type\": \"string\"\n    }\n  },\n  \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n}"
}
]
},
{
"id": 33,
"path": "customers",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/customers",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}",
"steps": [
{
"name": "createCustomer",
"type": "apiCall",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/customers",
"path": "customers",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
}
]
},
{
"id": 34,
"path": "accounts",
"method": "GET",
"serviceUrl": "http://localhost:9005/api/accounts",
"apiDocsUrl": "http://localhost:9005/v3/api-docs",
"responseSchema": "{\"type\":\"array\",\"items\":{\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}}}}",
"steps": [
{
"name": "fetchAccounts",
"type": "apiCall",
"serviceUrl": "http://localhost:9005/api/accounts",
"responseSchema": "{\"type\":\"array\",\"items\":{\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}}}}"
}
]
},
{
"id": 36,
"path": "accounts",
"method": "POST",
"serviceUrl": "http://localhost:9005/api/accounts",
"apiDocsUrl": "http://localhost:9005/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
"steps": [
{
"name": "createAccount",
"type": "apiCall",
"serviceUrl": "http://localhost:9005/api/accounts",
"path": "accounts",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
}
]
},
{
"id": 37,
"path": "customersAndAccounts/{customerId}/{accountId}",
"method": "GET",
"steps": [
{
"name": "extractVariables",
"type": "extractVariables",
"path": "customersAndAccounts/{customerId}/{accountId}"
},
{
"name": "renameCustomerId",
"type": "renameVariables",
"renameMappings": {
"customerId": "id"
}
},
{
"name": "callCustomerApi",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers/{id}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
},
{
"name": "renameAccountId",
"type": "renameVariables",
"renameMappings": {
"accountId": "id"
}
},
{
"name": "callAccountApi",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9005/api/accounts/{id}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
},
{
"name": "combineResponses",
"type": "combineResponses",
"combineStrategy": "merge"
}
]
},
{
"id": 39,
"path": "getCustomersAndAccounts",
"method": "POST",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNumber\":{\"type\":\"string\"},\"customerId\":{\"type\":\"string\"}},\"required\":[\"accountNumber\",\"customerId\"]}",
"steps": [
{
"name": "extractVariables",
"type": "extractVariables"
},
{
"name": "renameCustomerId",
"type": "renameVariables",
"renameMappings": {
"customerId": "id"
}
},
{
"name": "callCustomerApi",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers/{id}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
},
{
"name": "renameAccountId",
"type": "renameVariables",
"renameMappings": {
"accountNumber": "id"
}
},
{
"name": "callAccountApi",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9005/api/accounts/{id}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
},
{
"name": "combineResponses",
"type": "combineResponses",
"combineStrategy": "merge"
}
]
},
{
"id": 43,
"path": "customersAndAccounts/{accountId}",
"method": "GET",
"steps": [
{
"name": "extractAccountId",
"type": "extractVariables",
"path": "customersAndAccounts/{accountId}"
},
{
"name": "renameAccountIdForApiCall",
"type": "renameVariables",
"renameMappings": {
"accountId": "id"
}
},
{
"name": "callAccountApi",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9005/api/accounts/{id}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
},
{
"name": "renameCustomerId",
"type": "renameVariables",
"renameMappings": {
"$.callAccountApi.customerId": "id"
}
},
{
"name": "callCustomerApi",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers/{id}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
},
{
"name": "combineResponses",
"type": "combineResponses",
"combineStrategy": "merge"
}
]
}
]