* Start-up
  * Start Token Service - default Port 9001
  * Start Auth Service - default Port 9002
  * Start Customer Service - default Port 9003
  * Start Card Service - default Port 9004
  * Start Account Service - default Port 9005
  * Start KYC Service - default Port 9006
  * Start BFF Service - default Port 9007
  * Start gateway Service - default port 8081
* Login 
  * Hit http://localhost:8081/login.html with user/password as values
* Test
  * Hit http://localhost:8081/form.html and select get method and enter /customers in endpoint.
  Include Authorization Header
  Expect empty response to see we are able to hit customers service

  * In same, http://localhost:8081/form.html, select post method and enter /customers in endpoint
  Include Authorization Header
  enter below structure to create a customer
  {
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
  }
  and expect Id in respnse to verify customer is created successfully.

  * Try something like /customers/1 as a get request.
  * Try below sample payload to create a service config
    * method: post
    * endpoint url: /bff/api/service-configs
    * body:{
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
      "inputKey": null,
      "outputKey": "customerData",
      "responseKey": "customerData",
      "requestSchema": null,
      "responseSchema": "{\n  \"type\": \"object\",\n  \"properties\": {\n    \"id\": {\n      \"type\": \"integer\",\n      \"format\": \"int64\"\n    },\n    \"firstName\": {\n      \"type\": \"string\"\n    },\n    \"lastName\": {\n      \"type\": \"string\"\n    },\n    \"email\": {\n      \"type\": \"string\"\n    }\n  },\n  \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n}",
      "path": "customers/{id}"
      }
      ]
      }

    * also once more with:
      {
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
      "path": "",
      "responseSchema": "{\n  \"type\": \"array\",\n  \"items\": {\n    \"type\": \"object\",\n    \"properties\": {\n      \"id\": {\n        \"type\": \"integer\",\n        \"format\": \"int64\"\n      },\n      \"firstName\": {\n        \"type\": \"string\"\n      },\n      \"lastName\": {\n        \"type\": \"string\"\n      },\n      \"email\": {\n        \"type\": \"string\",\n        \"format\": \"email\"\n      }\n    },\n    \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n  }\n}"
      }
      ]
      }

    * and then call get to make sure that both above requests are saved.
  * hit /bff/customers/1 and /bff/customers to see that it is giving data as expected
    * Similarly create accounts config with:
      * {
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
        "path": "",
        "responseSchema": "{\"type\":\"array\",\"items\":{\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}}}}"
        }
        ]
        }

      * {
        "path": "accounts/{id}",
        "method": "GET",
        "serviceUrl": "http://localhost:9005/api/accounts/{id}",
        "apiDocsUrl": "http://localhost:9005/v3/api-docs",
        "responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}",
        "steps": [
        {
        "name": "fetchAccountById",
        "type": "apiCall",
        "serviceUrl": "http://localhost:9005/api/accounts/{id}",
        "path": "accounts/{id}",
        "responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
        }
        ]
        }

    * Try to create an account by hitting:
      * post with /accounts endpoint
      * with payload:
      * {
        "accountNumber": "0001",
        "accountType": "savings",
        "balance": 0.5,
        "customerId":1
        }
    * see that same account data is available on all of the endpoints 
      * /accounts, /accounts/1, /bff/accounts,/bff/accounts/1
    * Now to create a post request, add below service config:
      * {
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
        "serviceUrl": "http://localhost:9003/api/customers",
        "path": "customers",
        "requestSchema": "{\"type\":\"object\",\"properties\":{\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"firstName\",\"lastName\",\"email\"]}",
        "responseSchema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"firstName\":{\"type\":\"string\"},\"lastName\":{\"type\":\"string\"},\"email\":{\"type\":\"string\"}},\"required\":[\"id\",\"firstName\",\"lastName\",\"email\"]}"
        }
        ]
        }

      * {
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
        }

      * Then try to create customer and account using below sample requests:
      * {
        "firstName": "Joh22n3",
        "lastName": "Doe",
        "email": "john.doe@example.com"
        }
      * {
        "accountNumber": "0001",
        "accountType": "savings",
        "balance": 0.6,
        "customerId":2
        }


* BFF : WIP
  * Try hitting http://localhost:9007/bff/customers directly and see that it internally gets customers
  * TODO:
    * Get all endpoints of customers working by defining service config
    * Add other services like Cards and get all endpoints working
    * Create Jolt expressions for transformation of request / response
    * Create configurations for orchestrations 
    * Create configuration for compositions
    * Create UI for defining above
    * Create basci formulaes for data manipuations and their UI