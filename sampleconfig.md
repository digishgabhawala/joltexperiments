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
"method": "GET",
"serviceUrl": "http://localhost:9005/api/accounts",
"responseSchema": "{\"type\":\"array\",\"items\":{\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}}}}"
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
"name": "renameCustomerId",
"type": "renameVariables",
"mappings": {
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
"mappings": {
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
"combineStrategy": "merge",
"itemsList": [
"callCustomerApi",
"callAccountApi"
]
}
]
},
{
"id": 56,
"path": "customersAndAccounts/{customerId}/{accountId}",
"method": "GET",
"steps": [
{
"name": "renameCustomerId",
"type": "renameVariables",
"mappings": {
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
"mappings": {
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
"combineStrategy": "merge",
"itemsList": [
"callCustomerApi",
"callAccountApi"
]
}
]
},
{
"id": 57,
"path": "customersAndAccounts/{accountId}",
"method": "GET",
"steps": [
{
"name": "renameAccountIdForApiCall",
"type": "renameVariables",
"mappings": {
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
"mappings": {
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
"combineStrategy": "merge",
"itemsList": [
"callCustomerApi",
"callAccountApi"
]
}
]
},
{
"id": 59,
"path": "customers-and-accounts",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/customers-and-accounts",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"customer\":{\"type\":\"object\",\"properties\":{\"first_name\":{\"type\":\"string\"},\"last_name\":{\"type\":\"string\"},\"email\":{\"type\":\"object\",\"properties\":{\"address\":{\"type\":\"string\"}}}}},\"account\":{\"type\":\"object\",\"properties\":{\"accountNo\":{\"type\":\"string\"},\"typeOfAcc\":{\"type\":\"string\"},\"bal\":{\"type\":\"number\",\"format\":\"double\"}}}},\"required\":[\"customer\",\"account\"]}",
"steps": [
{
"name": "buildCustomerBody",
"type": "buildBody",
"mappings": {
"$.customer.first_name": "firstName",
"$.customer.last_name": "lastName",
"$.customer.email.address": "email"
}
},
{
"name": "createCustomer",
"type": "apiCall",
"method": "POST",
"body": "buildCustomerBody",
"serviceUrl": "http://localhost:9003/api/customers",
"path": "customers",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
},
{
"name": "buildAccountBody",
"type": "buildBody",
"mappings": {
"$.account.accountNo": "accountNumber",
"$.account.typeOfAcc": "accountType",
"$.account.bal": "balance",
"$.createCustomer.id": "customerId"
}
},
{
"name": "createAccount",
"type": "apiCall",
"method": "POST",
"body": "buildAccountBody",
"serviceUrl": "http://localhost:9005/api/accounts",
"path": "accounts",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
},
{
"name": "combineResponses",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"createCustomer",
"createAccount"
]
}
]
},
{
"id": 60,
"path": "createAccount1",
"method": "POST",
"serviceUrl": "http://localhost:9005/api/accounts",
"apiDocsUrl": "http://localhost:9005/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNo\":{\"type\":\"string\"},\"typeOfAcc\":{\"type\":\"string\"},\"bal\":{\"type\":\"number\",\"format\":\"double\"},\"custId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"accountNo\",\"typeOfAcc\",\"bal\",\"custId\"]}",
"steps": [
{
"name": "buildAccountBody",
"type": "buildBody",
"mappings": {
"$.accountNo": "accountNumber",
"$.typeOfAcc": "accountType",
"$.bal": "balance",
"$.custId": "customerId"
}
},
{
"name": "createAccount",
"type": "apiCall",
"method": "POST",
"body": "buildAccountBody",
"serviceUrl": "http://localhost:9005/api/accounts",
"path": "accounts",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
}
]
},
{
"id": 72,
"path": "searchCustomers",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/searchCustomers?customerName={customerName}&email={email}",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"finalResponseKey": "searchCustomers",
"steps": [
{
"name": "searchCustomersHeaders",
"type": "addHeaders",
"mappings": {
"$.email": "email",
"$.customerName": "customerName"
}
},
{
"name": "searchClientKey",
"type": "addVariables",
"mappings": {
"$.searchCustomersHeaders.key": "customer-client"
}
},
{
"name": "searchCustomers",
"type": "apiCall",
"method": "GET",
"headers": "searchCustomersHeaders",
"serviceUrl": "http://localhost:9003/api/customers/search?name={customerName}",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"path": "customers/search",
"responseSchema": "{\n  \"type\": \"array\",\n  \"items\": {\n    \"type\": \"object\",\n    \"properties\": {\n      \"id\": {\n        \"type\": \"integer\",\n        \"format\": \"int64\"\n      },\n      \"firstName\": {\n        \"type\": \"string\"\n      },\n      \"lastName\": {\n        \"type\": \"string\"\n      },\n      \"email\": {\n        \"type\": \"string\",\n        \"format\": \"email\"\n      }\n    },\n    \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n  }\n}"
}
]
},
{
"id": 73,
"path": "customers1",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/customers1",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"first_name\":{\"type\":\"string\"},\"last_name\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"first_name\",\"last_name\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}",
"steps": [
{
"name": "buildCustomerBody",
"type": "buildBody",
"mappings": {
"$.first_name": "firstName",
"$.last_name": "lastName",
"$.email": "email"
}
},
{
"name": "createCustomer",
"type": "apiCall",
"method": "POST",
"body": "buildCustomerBody",
"serviceUrl": "http://localhost:9003/api/customers",
"path": "customers",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
}
]
},
{
"id": 77,
"path": "customersWithHiddenId",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/customers1",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"first_name\":{\"type\":\"string\",\"maxLength\":20,\"minLength\":1},\"last_name\":{\"type\":\"string\",\"maxLength\":20,\"minLength\":1},\"email\":{\"type\":\"object\",\"properties\":{\"address\":{\"type\":\"string\",\"format\":\"email\"}},\"required\":[\"address\"]}},\"required\":[\"first_name\",\"last_name\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"createCustomer\":{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"},\"idHidden\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}}}",
"steps": [
{
"name": "buildCustomerBody",
"type": "buildBody",
"mappings": {
"$.first_name": "firstName",
"$.last_name": "lastName",
"$.email.address": "email"
}
},
{
"name": "createCustomer",
"type": "apiCall",
"method": "POST",
"body": "buildCustomerBody",
"serviceUrl": "http://localhost:9003/api/customers",
"path": "customers",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
},
{
"name": "deleteIdFromResponse",
"type": "deleteVariables",
"itemsList": [
"$.createCustomer.id"
]
},
{
"name": "addIdHiddenField",
"type": "addVariables",
"mappings": {
"$.createCustomer.idHidden": "true"
}
},
{
"name": "combineResponses",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"createCustomer"
]
}
]
},
{
"id": 100,
"path": "searchAndCreateAccount",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/searchAndCreateAccount",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"customer\":{\"type\":\"object\",\"properties\":{\"first_name\":{\"type\":\"string\"},\"last_name\":{\"type\":\"string\"},\"email\":{\"type\":\"object\",\"properties\":{\"address\":{\"type\":\"string\"}}}}},\"account\":{\"type\":\"object\",\"properties\":{\"accountNo\":{\"type\":\"string\"},\"typeOfAcc\":{\"type\":\"string\"},\"bal\":{\"type\":\"number\",\"format\":\"double\"}}}},\"required\":[\"customer\",\"account\"]}",
"steps": [
{
"name": "buildCustomerSearchHeaders",
"type": "addHeaders",
"mappings": {
"$.customer.first_name": "customerName",
"$.customer.email.address": "email"
},
"nextStep": "searchClientKey"
},
{
"name": "searchClientKey",
"type": "addVariables",
"mappings": {
"$.buildCustomerSearchHeaders.key": "customer-client"
},
"nextStep": "searchCustomer"
},
{
"name": "searchCustomer",
"type": "apiCall",
"method": "GET",
"headers": "buildCustomerSearchHeaders",
"serviceUrl": "http://localhost:9003/api/customers/search?name={$.customer.first_name}",
"path": "customers/search",
"responseSchema": "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\",\"format\":\"email\"}}}}",
"nextStep": "customerIdCondition"
},
{
"name": "customerIdCondition",
"type": "condition",
"condition": {
"key": "$.searchCustomer.length",
"operator": "GREATER_THAN",
"value": "0",
"ifStep": "assignExistingCustomerId",
"elseStep": "createNewCustomerComposite"
}
},
{
"name": "assignExistingCustomerId",
"type": "renameVariables",
"mappings": {
"$.searchCustomer[0].id": "existingCustomerId"
},
"nextStep": "buildAccountBodyWithExistingCustomerId"
},
{
"name": "createNewCustomerComposite",
"type": "composite",
"itemsList": [
"buildCustomerBody",
"createCustomer",
"assignNewCustomerId"
],
"nextStep": "buildAccountBodyWithNewCustomerId"
},
{
"name": "buildCustomerBody",
"type": "buildBody",
"mappings": {
"$.customer.first_name": "firstName",
"$.customer.last_name": "lastName",
"$.customer.email.address": "email"
},
"nextStep": "createCustomer"
},
{
"name": "createCustomer",
"type": "apiCall",
"method": "POST",
"body": "buildCustomerBody",
"serviceUrl": "http://localhost:9003/api/customers",
"path": "customers",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}}}",
"nextStep": "assignNewCustomerId"
},
{
"name": "assignNewCustomerId",
"type": "renameVariables",
"mappings": {
"$.createCustomer.id": "createdCustomerId"
},
"nextStep": "buildAccountBodyWithNewCustomerId"
},
{
"name": "buildAccountBodyWithNewCustomerId",
"type": "buildBody",
"mappings": {
"$.account.accountNo": "accountNumber",
"$.account.typeOfAcc": "accountType",
"$.account.bal": "balance",
"createdCustomerId": "customerId"
},
"nextStep": "combineAccountBodies"
},
{
"name": "buildAccountBodyWithExistingCustomerId",
"type": "buildBody",
"mappings": {
"$.account.accountNo": "accountNumber",
"$.account.typeOfAcc": "accountType",
"$.account.bal": "balance",
"existingCustomerId": "customerId"
},
"nextStep": "combineAccountBodies"
},
{
"name": "combineAccountBodies",
"type": "combineResponses",
"combineStrategy": "selectFirstNonNull",
"itemsList": [
"buildAccountBodyWithExistingCustomerId",
"buildAccountBodyWithNewCustomerId"
],
"nextStep": "createAccount"
},
{
"name": "createAccount",
"type": "apiCall",
"method": "POST",
"body": "combineAccountBodies",
"serviceUrl": "http://localhost:9005/api/accounts",
"path": "accounts",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}}}",
"nextStep": "combineResponses"
},
{
"name": "combineResponses",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"assignExistingCustomerId",
"assignNewCustomerId",
"createAccount"
]
}
]
},
{
"id": 113,
"path": "financialSummary",
"method": "POST",
"serviceUrl": "http://localhost:9003/api/financialSummary",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"customer\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}}}},\"required\":[\"customer\"]}",
"steps": [
{
"name": "buildCustomerSearchHeaders",
"type": "addHeaders",
"mappings": {
"$.customer.name": "customerName",
"$.customer.email": "email"
},
"nextStep": "searchClientKey"
},
{
"name": "searchClientKey",
"type": "addVariables",
"mappings": {
"$.buildCustomerSearchHeaders.key": "customer-client"
},
"nextStep": "searchCustomer"
},
{
"name": "searchCustomer",
"type": "apiCall",
"method": "GET",
"headers": "buildCustomerSearchHeaders",
"serviceUrl": "http://localhost:9003/api/customers/search?name={$.customer.name}",
"path": "customers/search",
"responseSchema": "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\",\"format\":\"email\"}}}}",
"nextStep": "extractCustomerIds"
},
{
"name": "extractCustomerIds",
"type": "renameVariables",
"mappings": {
"$.searchCustomer[*].id": "customerIds"
},
"nextStep": "getAllAccountIds"
},
{
"name": "getAllAccountIds",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9005/api/accounts",
"path": "accounts",
"responseSchema": "{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\"},\"customerId\":{\"type\":\"integer\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"}}}}",
"nextStep": "filterAccountsByCustomerId"
},
{
"name": "filterAccountsByCustomerId",
"type": "filterList",
"inputKey": "getAllAccountIds",
"condition": {
"key": "$.customerId",
"operator": "IN",
"value": "$.customerIds"
},
"nextStep": "calculateBalanceSummary"
},
{
"name": "calculateBalanceSummary",
"type": "renameVariables",
"mappings": {
"$.sum($.filterAccountsByCustomerId[*].balance)": "totalBalance"
},
"nextStep": "combineResults"
},
{
"name": "combineResults",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"filterAccountsByCustomerId",
"totalBalance"
]
}
]
},
{
"id": 114,
"path": "customers",
"method": "PUT",
"serviceUrl": "http://localhost:9003/api/customers",
"apiDocsUrl": "http://localhost:9003/v3/api-docs",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}",
"steps": [
{
"name": "updateCustomer",
"type": "apiCall",
"method": "PUT",
"serviceUrl": "http://localhost:9003/api/customers/{id}",
"path": "customers/{id}",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
}
]
},
{
"id": 127,
"path": "retrieveCustomerBasedonIndex",
"method": "GET",
"steps": [
{
"name": "addLoopProcessingIndex",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "0",
"finalResult": "[]",
"fetchCustomers": "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]",
"variableOne": "1"
},
"nextStep": "updateIndex"
},
{
"name": "updateIndex",
"type": "operate",
"operate": {
"op1": "loopProcessingIndex",
"op1Type": "INTEGER",
"operator": "add",
"op2": "variableOne",
"op2Type": "INTEGER",
"result": "loopProcessingIndex"
},
"nextStep": "fetchCurrentCustomer"
},
{
"name": "fetchCurrentCustomer",
"type": "operate",
"operate": {
"op1": "$.fetchCustomers[$.loopProcessingIndex]",
"op1Type": "JSON",
"operator": "JSONPATH_RESOLVE_INDEX",
"op2": "$.loopProcessingIndex",
"op2Type": "INTEGER",
"result": "currentCustomer"
},
"nextStep": "addCustomerToFinalResult"
},
{
"name": "addCustomerToFinalResult",
"type": "operate",
"operate": {
"op1": "$.finalResult",
"op1Type": "LIST",
"operator": "ADDTOJSONLIST",
"op2": "$.currentCustomer",
"op2Type": "JSON",
"result": "finalResult"
},
"nextStep": "returnProcessedData"
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult",
"loopProcessingIndex"
]
}
]
},
{
"id": 129,
"path": "retrieve2CustomersBasedonIndex",
"method": "GET",
"steps": [
{
"name": "addLoopProcessingIndex",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "0",
"finalResult": "[]",
"fetchCustomers": "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]",
"variableOne": "1"
},
"nextStep": "fetchCurrentCustomer"
},
{
"name": "fetchCurrentCustomer",
"type": "operate",
"operate": {
"op1": "$.fetchCustomers[$.loopProcessingIndex]",
"op1Type": "JSON",
"operator": "JSONPATH_RESOLVE_INDEX",
"op2": "$.loopProcessingIndex",
"op2Type": "INTEGER",
"result": "currentCustomer"
},
"nextStep": "addCustomerToFinalResult"
},
{
"name": "addCustomerToFinalResult",
"type": "operate",
"operate": {
"op1": "$.finalResult",
"op1Type": "LIST",
"operator": "ADDTOJSONLIST",
"op2": "$.currentCustomer",
"op2Type": "JSON",
"result": "finalResult"
},
"nextStep": "increaseIndex"
},
{
"name": "increaseIndex",
"type": "operate",
"operate": {
"op1": "loopProcessingIndex",
"op1Type": "INTEGER",
"operator": "add",
"op2": "variableOne",
"op2Type": "INTEGER",
"result": "loopProcessingIndex"
},
"nextStep": "fetchCurrentCustomer2"
},
{
"name": "fetchCurrentCustomer2",
"type": "operate",
"operate": {
"op1": "$.fetchCustomers[$.loopProcessingIndex]",
"op1Type": "JSON",
"operator": "JSONPATH_RESOLVE_INDEX",
"op2": "$.loopProcessingIndex",
"op2Type": "INTEGER",
"result": "currentCustomer"
},
"nextStep": "addCustomerToFinalResult2"
},
{
"name": "addCustomerToFinalResult2",
"type": "operate",
"operate": {
"op1": "$.finalResult",
"op1Type": "LIST",
"operator": "ADDTOJSONLIST",
"op2": "$.currentCustomer",
"op2Type": "JSON",
"result": "finalResult"
},
"nextStep": "returnProcessedData"
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult",
"loopProcessingIndex"
]
}
]
},
{
"id": 131,
"path": "seeIndexIncreasingby2",
"method": "GET",
"steps": [
{
"name": "addLoopProcessingIndex",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "1",
"finalResult": "{}",
"variableOne": "2"
},
"nextStep": "updateIndex"
},
{
"name": "updateIndex",
"type": "operate",
"operate": {
"op1": "loopProcessingIndex",
"op1Type": "INTEGER",
"operator": "add",
"op2": "variableOne",
"op2Type": "INTEGER",
"result": "loopProcessingIndex"
},
"nextStep": "returnProcessedData"
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult",
"loopProcessingIndex"
]
}
]
},
{
"id": 133,
"path": "seeIndexIncreasing",
"method": "GET",
"steps": [
{
"name": "addLoopProcessingIndex",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "0",
"finalResult": "{}",
"variableOne": "1"
},
"nextStep": "updateIndex"
},
{
"name": "updateIndex",
"type": "operate",
"operate": {
"op1": "loopProcessingIndex",
"op1Type": "INTEGER",
"operator": "add",
"op2": "variableOne",
"op2Type": "INTEGER",
"result": "loopProcessingIndex"
},
"nextStep": "returnProcessedData"
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult",
"loopProcessingIndex"
]
}
]
},
{
"id": 134,
"path": "updateCustomerName",
"method": "GET",
"steps": [
{
"name": "addLoopProcessingIndex",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "0",
"finalResult": "[]",
"fetchCustomers": "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]",
"variableOne": "1",
"nameSuffix": "_new"
},
"nextStep": "fetchCurrentCustomer"
},
{
"name": "fetchCurrentCustomer",
"type": "operate",
"operate": {
"op1": "$.fetchCustomers[$.loopProcessingIndex]",
"op1Type": "JSON",
"operator": "JSONPATH_RESOLVE_INDEX",
"op2": "$.loopProcessingIndex",
"op2Type": "INTEGER",
"result": "currentCustomer"
},
"nextStep": "updateCustomerName"
},
{
"name": "updateCustomerName",
"type": "operate",
"operate": {
"op1": "$.currentCustomer.firstName",
"op1Type": "STRING",
"operator": "add",
"op2": "nameSuffix",
"op2Type": "STRING",
"result": "$.currentCustomer.firstName"
},
"nextStep": "addCustomerToFinalResult"
},
{
"name": "addCustomerToFinalResult",
"type": "operate",
"operate": {
"op1": "$.finalResult",
"op1Type": "LIST",
"operator": "ADDTOJSONLIST",
"op2": "$.currentCustomer",
"op2Type": "JSON",
"result": "finalResult"
},
"nextStep": "returnProcessedData"
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult",
"loopProcessingIndex"
]
}
]
},
{
"id": 145,
"path": "updateCustomerName1",
"method": "GET",
"steps": [
{
"name": "addLoopProcessingIndex",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "0",
"finalResult": "[]",
"fetchCustomers": "[{\"id\": 1,\"firstName\": \"John\",\"lastName\": \"Doe\",\"email\": \"johndoe@example.com\"},{\"id\": 2,\"firstName\": \"John2\",\"lastName\": \"Doe\",\"email\": \"johndoe2@example.com\"}]",
"variableOne": "1",
"nameSuffix": "_new"
},
"nextStep": "fetchCurrentCustomer"
},
{
"name": "fetchCurrentCustomer",
"type": "operate",
"operate": {
"op1": "$.fetchCustomers[$.loopProcessingIndex]",
"op1Type": "JSON",
"operator": "JSONPATH_RESOLVE_INDEX",
"op2": "$.loopProcessingIndex",
"op2Type": "INTEGER",
"result": "currentCustomer"
},
"nextStep": "updateCustomerName"
},
{
"name": "updateCustomerName",
"type": "operate",
"operate": {
"op1": "$.currentCustomer.firstName",
"op1Type": "STRING",
"operator": "add",
"op2": "nameSuffix",
"op2Type": "STRING",
"result": "$.currentCustomer.firstName"
},
"nextStep": "addCustomerToFinalResult"
},
{
"name": "addCustomerToFinalResult",
"type": "operate",
"operate": {
"op1": "$.finalResult",
"op1Type": "LIST",
"operator": "ADDTOJSONLIST",
"op2": "$.currentCustomer",
"op2Type": "JSON",
"result": "finalResult"
},
"nextStep": "returnProcessedData"
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult",
"loopProcessingIndex"
]
}
]
},
{
"id": 146,
"path": "updateAllCustomers",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/updateAllCustomers",
"steps": [
{
"name": "initializeVariables",
"type": "addVariables",
"mappings": {
"loopProcessingIndex": "0",
"finalResult": "[]",
"variableOne": "1",
"nameSuffix": "_new"
},
"nextStep": "fetchCustomers"
},
{
"name": "fetchCustomers",
"type": "apiCall",
"method": "GET",
"serviceUrl": "http://localhost:9003/api/customers",
"nextStep": "loopProcessing"
},
{
"name": "loopProcessing",
"type": "composite",
"itemsList": [
"fetchCurrentCustomer",
"updateIndex",
"updateCustomerName",
"buildCustomerBody",
"updateCustomer",
"addCustomerToFinalResult"
],
"nextStep": "checkIfMoreCustomers"
},
{
"name": "fetchCurrentCustomer",
"type": "operate",
"operate": {
"op1": "$.fetchCustomers[$.loopProcessingIndex]",
"op1Type": "JSON",
"operator": "JSONPATH_RESOLVE_INDEX",
"op2": "$.loopProcessingIndex",
"op2Type": "INTEGER",
"result": "currentCustomer"
},
"nextStep": "updateIndex"
},
{
"name": "updateIndex",
"type": "operate",
"operate": {
"op1": "$.loopProcessingIndex",
"op1Type": "INTEGER",
"operator": "add",
"op2": "variableOne",
"op2Type": "INTEGER",
"result": "loopProcessingIndex"
},
"nextStep": "updateCustomerName"
},
{
"name": "updateCustomerName",
"type": "operate",
"operate": {
"op1": "$.currentCustomer.firstName",
"op1Type": "STRING",
"operator": "add",
"op2": "$.nameSuffix",
"op2Type": "STRING",
"result": "$.currentCustomer.firstName"
},
"nextStep": "buildCustomerBody"
},
{
"name": "buildCustomerBody",
"type": "buildBody",
"mappings": {
"$.currentCustomer.id": "id",
"$.currentCustomer.firstName": "firstName",
"$.currentCustomer.lastName": "lastName",
"$.currentCustomer.email": "email"
},
"nextStep": "updateCustomer"
},
{
"name": "updateCustomer",
"type": "apiCall",
"method": "PUT",
"body": "buildCustomerBody",
"serviceUrl": "http://localhost:9003/api/customers/{$.currentCustomer.id}",
"requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
"responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}",
"nextStep": "addCustomerToFinalResult"
},
{
"name": "addCustomerToFinalResult",
"type": "operate",
"operate": {
"op1": "$.finalResult",
"op1Type": "LIST",
"operator": "ADDTOJSONLIST",
"op2": "$.currentCustomer",
"op2Type": "JSON",
"result": "finalResult"
},
"nextStep": "checkIfMoreCustomers"
},
{
"name": "checkIfMoreCustomers",
"type": "condition",
"condition": {
"key": "$.length($..fetchCustomers)",
"operator": "GREATER_THAN",
"value": "$.loopProcessingIndex",
"ifStep": "loopProcessing",
"elseStep": "returnProcessedData"
}
},
{
"name": "returnProcessedData",
"type": "combineResponses",
"combineStrategy": "merge",
"itemsList": [
"finalResult"
]
}
]
}
]