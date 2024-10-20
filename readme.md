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
      "serviceUrl": "http://localhost:9003/api/customers/{id}",
      "apiDocsUrl": "http://localhost:9003/v3/api-docs",
      "schema": "{\n  \"type\": \"object\",\n  \"properties\": {\n    \"id\": {\n      \"type\": \"integer\",\n      \"format\": \"int64\"\n    },\n    \"firstName\": {\n      \"type\": \"string\"\n    },\n    \"lastName\": {\n      \"type\": \"string\"\n    },\n    \"email\": {\n      \"type\": \"string\"\n    }\n  },\n  \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n}"
      }
    * also once more with:
      {
      "path": "customers",
      "serviceUrl": "http://localhost:9003/api/customers",
      "apiDocsUrl": "http://localhost:9003/v3/api-docs",
      "schema": "{\n  \"type\": \"array\",\n  \"items\": {\n    \"type\": \"object\",\n    \"properties\": {\n      \"id\": {\n        \"type\": \"integer\",\n        \"format\": \"int64\"\n      },\n      \"firstName\": {\n        \"type\": \"string\"\n      },\n      \"lastName\": {\n        \"type\": \"string\"\n      },\n      \"email\": {\n        \"type\": \"string\"\n      }\n    },\n    \"required\": [\"id\", \"firstName\", \"lastName\", \"email\"]\n  }\n}"
      }
    * and then call get to make sure that both above requests are saved.
  * hit /bff/customers/1 and /bff/customers to see that it is giving data as expected
    * Similarly create accounts config with:
      * {
        "path": "accounts",
        "serviceUrl": "http://localhost:9005/api/accounts",
        "apiDocsUrl": "http://localhost:9005/v3/api-docs",
        "schemaUrl": "http://localhost:9005/v3/api-docs",
        "schema": "{\"type\":\"array\",\"items\":{\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}}}}"
        }
      * {
        "path": "accounts/{id}",
        "serviceUrl": "http://localhost:9005/api/accounts/{id}",
        "apiDocsUrl": "http://localhost:9005/v3/api-docs",
        "schema": "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\",\"format\":\"int64\"},\"accountNumber\":{\"type\":\"string\"},\"accountType\":{\"type\":\"string\"},\"balance\":{\"type\":\"number\",\"format\":\"double\"},\"customerId\":{\"type\":\"integer\",\"format\":\"int64\"}},\"required\":[\"id\",\"accountNumber\",\"accountType\",\"balance\",\"customerId\"]}"
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