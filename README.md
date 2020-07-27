# Overview
### Microservices
[__*Currency Converter Microservice*__](#currency-converter-microservice) provides REST service converting a currency into another currency. It invokes services from [__*Currency Exchange Microservice*__](#currency-exchange-microservice) and [__*Limits Microservice*__](#limits-microservice) to retrieve respectively the exchange rate and the limit amount (minimum and maximum) for each exchange.  
  
### Cloud Servers
- [__*Spring Cloud Config Server:*__](#spring-cloud-config-server) contains configurations of all the Microservice. It connects to a Config Git Repository as a storage.
- [__*Eureka Name Server:*__](#eureka-name-server) registers and manages all the instances of Microservice
- [__*Zuul API Gateway Server:*__](#zuul-api-gateway-server) used for logging all the requests made between Microservices
- [__*Zipkin Distributed Tracing Server:*__](#zipkin-distributed-tracing-server) stores all tracing requests (by Sleuth) in one place

### Other Cloud components
- [__*OpenFeign:*__](#openfeign) leverages invoking REST API from other Microservices
- [__*Ribbon:*__](#ribbon) client-side load-balancing, used for the Microservices which invoking API from mulitple instances of other Microservice
- [__*Spring Cloud Sleuth:*__](#spring-cloud-sleuth) adds a unique ID to a request to trace it across multiple Microservices
- [__*Spring Cloud Bus:*__](#spring-cloud-bus) updates (broadcasts) changes from the Config Git Repo of the Spring Cloud Config Server to multiple Microservices at the same time
- [__*Hystrix:*__](#hystrix) provides fault tolerance (return a default response when the Microservice is not available)

### Dependencies
- Spring Config Client/Server
- OpenFeign
- Ribbon
- Eureka Server
- Eureka Discovery Client: connect to Euraka Name Server for loading balancing
- Zuul
- Spring RabbitMQ Support: support a message queue supporting for Zipkin Distributed Tracing Server
- Sleuth: distributed tracing via logs with Spring Cloud Sleuth
[[URL](https://mvnrepository.com/artifact/org.springframework.amqp/spring-rabbit)]   
- Spring Cloud Starter Bus AMQP: for Spring Cloud Bus 
[[URL](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-bus-amqp)]
- Hystrix

---

# Development Process

## Microservices
### [Limits Microservice](https://github.com/cpulover-practice/spring-cloud/tree/master/limits-microservice)
1. Setup Microservice 
   1. Spring Boot dependencies:
      - Spring Web
      - Spring Config Client
      - Spring Cloud Starter Bus AMQP
      - Hystrix
      - Assistant dependencies: Spring Boot DevTools, Actuator, Lombok
   2. Configuration 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/tree/master/limits-microservice/src/main/resources)]
      - Application name
      - Port
      - Other properties
      
### [Currency Exchange Microservice](https://github.com/cpulover-practice/spring-cloud/tree/master/currency-exchange-microservice)
1. Setup Microservice 
   1. Spring Boot dependencies:
      - Spring Web
      - Spring Config Client
      - Eureka Discovery Client
      - Sleuth
      - Spring RabbitMQ Support 
      - Spring Data JPA
      - H2 Database
      - Assistant dependencies: Spring Boot DevTools, Actuator, Lombok
   2. Configuration 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/resources/application.properties)]
      - Application name
      - Port
2. Setup Database 
   1. Populate data 
[[data.sql](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/resources/data.sql)]
   2. Create Entity for service response
[[ExchangeValue](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/java/com/cpulover/microservices/entity/ExchangeValue.java)]
   3. Create JPA repository for the Entity
[[ExchangeValueRepository](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/java/com/cpulover/microservices/repository/ExchangeValueRepository.java)]
3. Create services to CRUD Entity contents
[[CurrencyExchangeMicroserviceRestController](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/java/com/cpulover/microservices/controller/CurrencyExchangeRestController.java)]

### [Currency Converter Microservice](https://github.com/cpulover-practice/spring-cloud/tree/master/currency-converter-microservice)
1. Setup Microservice 
   1. Spring Boot dependencies:
      - OpenFeign
      - Ribbon
      - Eureka Discovery Client
      - Sleuth: distributed tracing via logs with Spring Cloud Sleuth
      - Spring RabbitMQ Support 
      - Spring Web
      - Spring Config Client
      - Assistant dependencies: Spring Boot DevTools, Actuator, Lombok
   2. Configuration 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/resources/application.properties)]
      - Application name
      - Port
2. Create Entity for service response 
[[CurrencyConversion](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/entity/CurrencyConversion.java)]
3. Create services to CRUD Entity contents
[[CurrencyConversionRestController](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/controller/CurrencyConversionRestController.java)]
     - Create a Rest Template (to invoke service of the Currency Exchange Microservice) {prefer alternative: [Feign](#openfeign)}
     
## Cloud Servers
### [Spring Cloud Config Server](https://github.com/cpulover-practice/spring-cloud/tree/master/spring-cloud-config-server)
1. Setup Server 
   1. Spring Boot dependencies:
      - Spring Config Server
      - Spring Boot DevTools
      - Spring Cloud Starter Bus AMQP
   2. Configuration 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/spring-cloud-config-server/src/main/resources/application.properties)]
      - Application name
      - Port
   3. Enable Spring Cloud Config Server with __*@EnableConfigServer*__  
[[SpringCloudConfigServerApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/spring-cloud-config-server/src/main/java/com/cpulover/microservices/SpringCloudConfigServerApplication.java)]

2. Create git repository (to store configuration files of Microservices) 
[[config-git-repo](https://github.com/cpulover-practice/config-git-repo)]

3. Create property files of diferent profiles for Microservices inside the Config Git Repo 
[[limits-microservice.properties](https://github.com/cpulover-practice/config-git-repo/blob/master/limits-microservice.properties)] 
[[limits-microservice-dev.properties](https://github.com/cpulover-practice/config-git-repo/blob/master/limits-microservice-dev.properties)] 
[[limits-microservice-qa.properties](https://github.com/cpulover-practice/config-git-repo/blob/master/limits-microservice-qa.properties)]
   - File naming: ```<microservice_name>-<profile>.properties``` (<```microservice_name>``` must match to the application name of the service configured in the servicer properties file)
   - Commit the files

4. Connect Config Server to Config Git Repo 
   1. Link to the Repo in Eclipse: ```Choose Config Server project -> Build Path  -> Source -> Link Source -> Choose Config Git folder```
   2. Configure git URL (local or remote) in Server property file (use ```/``` instead of ```\``` ???) 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/spring-cloud-config-server/src/main/resources/application.properties)]
   3. Test the connection between Config Server and Config Git Repo:
      1. Run Java application for the Config Server
      2. Access URL: ```localhost:<server_port>/<microservice_name>/<profile>```

5. Connect Microservices to the Config Server
   1. Configure Microservices 
   [[bootstrap.application](https://github.com/cpulover-practice/spring-cloud/blob/master/limits-microservice/src/main/resources/bootstrap.properties)]
      - Rename the application.properties to bootstrap.properties (to prevent local default properties file for the service) 
      - {Remove application properties which is configured in the Config Git Repo} 
      - Declare URI of the Config Server
      - Declare the profile for the configuration {if not declare, default is picked up}
   2. Test the connection between Config Server and Microservice
      1. Run Java application for the Config Server
      2. Run Java application for the Microservices
      3. Access Microservice endpoints which exposes the property values
      
### [Eureka Name Server](https://github.com/cpulover-practice/spring-cloud/tree/master/eureka-name-server)
1. Setup Server 
[[eureka-name-server]()]
   1. Spring Boot dependencies:
      - Eureka Server
      - Spring Config Client
      - Assistant dependencies: Spring Boot DevTools, Actuator, Lombok
   2. Configuration 
      1. Activate Server configuration with __*@EnableEurekaServer*__ 
[[EurekaNameServerApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/eureka-name-server/src/main/java/com/cpulover/microservices/EurekaNameServerApplication.java)]
      2. Configure in 
[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/eureka-name-server/src/main/resources/application.properties)
         - Application name  
         - Port (typically 8761)
2. Connect Eureka Server to Microservices
   1. Configure Microservices
      - Add Eureka Discovery Client dependency
      - Register to the Server with __*@EnableDiscoveryClient*__ 
[[CurrencyConverterMicroserviceApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/CurrencyConverterMicroserviceApplication.java)] 
[[CurrencyExchangeMicroserviceApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/java/com/cpulover/microservices/CurrencyExchangeMicroserviceApplication.java)]
      - Declare Eureka Server URL in 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/resources/application.properties)] 
[[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/resources/application.properties)]
   2. Test the connection
      - Run the Eureka Server
      - Run all Microservices
      - Access URL: ```localhost:<eureka_server_port>```

### [Zuul API Gateway Server](https://github.com/cpulover-practice/spring-cloud/tree/master/zuul-api-gateway-server)
1. Setup Server 
   1. Spring Boot dependencies:
      - Zuul
      - Eureka Discovery Client
      - Sleuth
      - Zipkin Client
      - Spring RabbitMQ Support 
      - Assistant dependencies: Spring Boot DevTools, Actuator, Lombok
   2. Configuration 
      - Enable Zuul Proxy with __*@EnableZuulProxy*__
      - Register to Eureka Server with __*@EnableDiscoveryClient*__ 
[[ZuulApiGatewayServerApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/zuul-api-gateway-server/src/main/java/com/cpulover/microservices/ZuulApiGatewayServerApplication.java)]
      - Configure in 
[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/zuul-api-gateway-server/src/main/resources/application.properties)
        - Application name
        - Port
        - Eureka Server URL
2. Create Filter class extends ZuulFilter with __*@Component*__ 
[[ZuulLoggingFilter](https://github.com/cpulover-practice/spring-cloud/blob/master/zuul-api-gateway-server/src/main/java/com/cpulover/microservices/filter/ZuulLoggingFilter.java)]
   - Implement abstract methods:
     - ```filterOrder()```: priority when there are multiple Filters
     - ```shouldFilter()```: excecute the filter for every request or not (invoke the ```run()```)
     - ```run()```: core method of the filter handling bussiness logic
     - ```filterType()```: when to execute the filter
   - Get the current request using RequestContext

3. Setup Zuul API Gateway between Microservices: update Feign Proxy interface 
[[CurrencyExchangeServiceProxy](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/proxy/CurrencyExchangeServiceProxy.java)]
   - Connect to the Zuul Gateway by __*@FeignClient*__ instead to other Microservice
   - Update the endpoint mapping of Zuul Gateway: append the Microsevice name (which owns the invoked REST API) at the beginning
   
4. Test the Filter
   - Run Eureka Server -> Microservices -> Zuul Server
   - Access Zuul Gateway URL directly: ```localhost:<zuul_server_port>/<microservice_name/<microservice_endpoint>```
     - E.g: ```http://localhost:8000/currency-exchange/from/BBB/to/CCC``` => ```http://localhost:8765/currency-exchange-microservice/currency-exchange/from/BBB/to/CCC```
   - Access endpoints which invoke REST API from other Microservice through Zuul Gateway
   
### Zipkin Distributed Tracing Server
1. Setup enviroment
   1. Install Erlang and RabbitMQ: message queue supporting asynchronous communication for Zipkin to pick up
   2. Get Zipkin server jar file 
   [[URL](https://github.com/cpulover-practice/spring-cloud/blob/master/zipkin-server-2.12.9-exec.jar)]

2. Connect Microservices to RabbitMQ and Zipkin: add Zipkin Client and Spring RabbitMQ Support dependencies
3. Run the Zinkin server by CLI
   - ```set RABBIT_URI=amqp://localhost```: connect Zipkin to RabbitMQ Server
   - ```java -jar <zipkin_jar_file_name>```
4. Access Zinkin UI via URL: ```http://localhost:9411/zipkin```

## Other Cloud Components
### OpenFeign
For Microservices which invoke other Microservice's API 
1. Add OpenFeign dependency
2. Enable OpenFeign with __*@EnableFeignClients*__ 
[[CurrencyConverterMicroserviceApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/CurrencyConverterMicroserviceApplication.java)]
3. Create a Proxy interface 
[[CurrencyExchangeServiceProxy](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/proxy/CurrencyExchangeServiceProxy.java)]
   - __*@FeignClient*__  with *```name```* of the Microservice
   - Declare *```url```* if connect to a single instance. Use [Ribbon](#ribbon) if connect to many instances.
   - Declare method map to the desired API
4. Inject the Proxy into the Rest Controller with __*@Autowired*__ then invoke Proxy method in the services  
[[CurrencyConversionRestController](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/controller/CurrencyConversionRestController.java)]

### Ribbon
For Microservices which invoke other Microservice's (target) API 
1. Add Ribbon dependency
2. Declare the Microservice target with __*@RibbonClient*__ in the Proxy  
[[CurrencyExchangeServiceProxy](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/proxy/CurrencyExchangeServiceProxy.java)]
3. Configure URLs of multiple instances of the Microservice target in 
[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/resources/application.properties) 
{prefer alternative: [Name Server](#eureka-name-server}

### Spring Cloud Sleuth
1. Add Sleuth dependency
2. Create Sample Bean (brave.sampler) in the application (which owns services needed to trace) 
[[ZuulApiGatewayServerApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/zuul-api-gateway-server/src/main/java/com/cpulover/microservices/ZuulApiGatewayServerApplication.java)] 
[[CurrencyConverterMicroserviceApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-converter-microservice/src/main/java/com/cpulover/microservices/CurrencyConverterMicroserviceApplication.java)] 
[[CurrencyExchangeMicroserviceApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/currency-exchange-microservice/src/main/java/com/cpulover/microservices/CurrencyExchangeMicroserviceApplication.java)] 
   
### Spring Cloud Bus
For Microservices which are configured externally by the Config Server
1. Ensure RabbitMQ Server is running
2. Actuator, Spring Config Client and Spring Cloud Starter Bus AMQP dependencies for Config Clients (Microservices)
3. Spring Cloud Starter Bus AMQP dependency for Config Server
4. Enable Actuator endpoints for Config Clients in 
[application.properties](https://github.com/cpulover-practice/spring-cloud/blob/master/limits-microservice/src/main/resources/bootstrap.properties)
5. For updating the changes from Config Git Repo
   - Commit the Git Repo
   - ```POST localhost:<microservice_port>/actuator/bus-refresh``` (if there are many instances of a Microservices on different ports, choose any port)

### Hystrix
1. Add Hystrix dependency
2. Enable Hystrix with __*@EnableHystrix*__ 
[[LimitsMicroserviceApplication](https://github.com/cpulover-practice/spring-cloud/blob/master/limits-microservice/src/main/java/com/example/microservices/LimitsMicroserviceApplication.java)]
3. Update the Rest Controller 
[[LimitConfigRestController](https://github.com/cpulover-practice/spring-cloud/blob/master/limits-microservice/src/main/java/com/example/microservices/controller/LimitConfigRestController.java)]
   - Specify a fallback method for an endpoint with __*@HystrixCommand*__ 
   - Define the fallback method

---

# Notes - Tips
- [Spring Cloud] For every change in the Config Git Repo, to apply the change on Microservices, need to commit the Git Repo and: 
  - Option 1: Restart the Microservices (or Config Server???) 
  - Option 2: Use Actuator endpoint for each Microservice (or instance): ```POST localhost:<microservice_port>/actuator/refresh```
  - Option 3: Use Spring Cloud Bus endpoint for all Microservices (or instances): ```POST localhost:<microservice_port>/actuator/bus-refresh``` (prefer for a large number of Microservices or instances)
- [Spring] ```bootstrap.properties``` used for Spring Cloud has higher priority than ```application.properties``` used in Spring Boot
- [Spring Core] Inject properties for Microservices:
  1. Declare application properties (for injection instead of hard-coding)  
[[application.properties]()]
  2. Create a Configuration class with __*@ConfigurationProperties*__ and __*@Component*__ (to read the properties) 
[[Configuration]()] 
     - Add prefix (?)
     - Declare variables matching the needed properties
     - Create setter and getter or use __*@Data*__
  3. Inject the Configuration in Controllers using __*@Autowired*__ (to get the properties in application.properties) 
[[LimitsMicroserviceApplication]()]
- [Spring Core] Prefer injecting properties by create a Configuration class to using @Value
- [Spring] To simulate in-memory database for protyping
  - Use __*@PostConstruct*__ in the Controller
  - Use Command Line Runner to populate data at the begging of the application
  - Use H2 database with a script in resources directory
- [Spring Core] Retrieve application port by injecting Enviroment [springframework.core] 
[[CurrencyExchangeMicroserviceApplication]()]
- [Esclipse] Create multiple instances of Microservice on different ports (set port for application externally):
  1. ```Choose service project -> Run As -> Run Configurations```
  2. Duplicate the service configuration in Java application
  3. Selet Arguments tag, for VM arguments: ```-Dserver.port=<port>```
- [Eureka] Eureka clients use ```jackson-dataformat-xml```. To enable JSON format, exclude xml dependency when adding Eureka Client dependency 
[[pom.xml]()]
- [Spring Cloud] Order of application execution: Name Server -> Zipkin Server (CLI) -> Microservices -> Zuul API Gateway Server
- [RabbitMQ] RabbitMQ Server runs as a service on background in Windows
- [H2] Use ```' '``` instead of ```" "``` in the script











