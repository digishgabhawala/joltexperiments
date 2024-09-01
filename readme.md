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