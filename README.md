# First Step

1. Setup Microservice 
[[limits-microservice]()]
   1. Spring Boot dependencies:
      - Spring Web
      - Spring Config Client
      - Spring Boot DevTools
      - Actuator
      - Lombok
   2. Configuration 
[[application.properties]()]
      - Application name
      - Port
      - Other properties

2. Setup Spring Cloud Config Server 
[[spring-cloud-config-server]()]
   1. Spring Boot dependencies:
      - Spring Config Server
      - Spring Boot DevTools
   2. Configuration 
[[application.properties]()]
      - Application name
      - Port
      - Other properties
   3. Enable Spring Cloud Config Server with @EnableConfigServer 
[[SpringCloudConfigServerApplication]()]

3. Create git repository to store configurations of microservices 
[[config-git-repo]()]

4. Create property file for Microservices inside the Config Git Repo 
[[limits-microservice.properties]()] [[limits-microservice-dev.properties]()] [[limits-microservice-qa.properties]()]
   - File naming: ```<microservice_name>-<profile>.properties``` (<```microservice_name>``` must match to the application name of the service configured in the servicer properties file)
   - Commit the files

5. Connect Config Server to Config Git Repo 
   1. Link to the Repo: ```Choose Config Server project -> Build Path  -> Source -> Link Source -> Choose Config Git folder```
   2. Configure git URL (local or remote) in Server properties (use ```/``` instead of ```\```) 
[[application.properties]()]
   3. Test the connection between Config Server and Config Git Repo:
      1. Run Java application for the Config Server
      2. Access URL: ```localhost:<server_port>/<microservice_name>/<profile>```

6. Connect Microservices to the Config Server
   1. Setup the Microservice
      - Rename the application.properties to bootstrap.properties (to prevent local default properties file for the service) 
[[bootstrap.application]()]
      - Remove application properties which is configured in the Config Git Repo
      - Declare URI of the Config Server: ```http://localhost:<server_port>```
      - Declare the profile for the configuration (if not declare, default is picked up)
   2. Test the connection between Config Server and Microservice
      1. Run Java application for the Config Server
      2. Run Java application for the Microservice
      3. Access the Microservice endpoints which exposes the property values

---

# More Microservices
### Currency Exchange Microservice
1. Setup Microservice 
[[currency-exchange-microservice]()]
   1. Spring Boot dependencies:
      - Spring Web
      - Spring Config Client
      - Eureka Discovery Client: connect to Euraka Name Server for loading balancing 
      - Spring Data JPA
      - H2 Database
      - Spring Boot DevTools
      - Actuator
      - Lombok
   2. Configuration 
[[application.properties]()]
      - Application name
      - Port

2. Setup Database stuffs 
   - Entity response
[[ExchangeValue]()]
   - Data population 
[[data.sql]()]
   - JPA repository 
[[ExchangeValueRepository]()]
3. Create services 
[[CurrencyExchangeMicroserviceRestController]()]


### Currency Converter Microservice
1. Setup Microservice 
[[currency-converter-microservice]()]
   1. Spring Boot dependencies:
      - OpenFeign: leverage invoking REST API from other Microservices
      - Ribbon: client-side load-balancing, used for the Microservices which invoking API from other Microservices
      - Eureka Discovery Client: connect to Euraka Name Server for loading balancing 
      - Spring Web
      - Spring Config Client
      - Spring Boot DevTools
      - Actuator
      - Lombok
   2. Configuration 
[[application.properties]()]
      - Application name
      - Port
2. Create Entity response 
[[CurrencyConversion]()]
3. Create services
[[CurrencyConversionRestController]()]
     - Create a Rest Template to invoke service of the Currency Exchange {prefer alternative: OpenFeign - 4}
4. OpenFeign
   1. Enable OpenFeign with @EnableFeignClients 
[[CurrencyConverterMicroserviceApplication]()]
   2. Create a Proxy interface for the Microservice from which need to invoke REST API
      - @FeignClient with name and url {if connect to a single instance} of the Microservice
      - Declare method map to the desired API
   3. Inject the Proxy into the Rest Controller with @Autowired
[[CurrencyConversionRestController]()]
5. Ribbon: connect to multiple instances of the Microservice, for load distribution
   1. Enable Ribon with @RibbonClient in the Proxy 
[[CurrencyExchangeServiceProxy]()]
   2. Configure URLs of multiple instances in {prefer alternative: Name Server}
[application.properties]()

### Eureka Name Server
1. Setup Server 
[[eureka-name-server]()]
   1. Spring Boot dependencies:
      - Eureka Server
      - Spring Config Client
      - Spring Boot DevTools
      - Actuator
      - Lombok
   2. Configuration 
      1. Activate Server configuration with @EnableEurekaServer 
[[EurekaNameServerApplication]()]
      2. Configure in 
[application.properties]()
         - Application name  
         - Port (typically 8761)
2. Connect Eureka Server to Microservices
   1. Configure Microservices
      - Eureka Discovery Client dependency
      - Register to the Server with @EnableDiscoveryClient 
[[CurrencyConverterMicroserviceApplication]()] 
[[CurrencyExchangeMicroserviceApplication]()]
      - Configure Eureka Server URL in 
[application.properties]()] 
[application.properties]()]
   2. Test the connection
      - Run the Eureka Server
      - Run all the Microservices
      - Access URL: ```localhost:<eureka_server_port>```

### Zuul API Gateway Server
1. Setup Server 
[[zuul-api-gateway-server]()]
   1. Spring Boot dependencies:
      - Zuul
      - Eureka Discovery Client: connect to Euraka Name Server for loading balancing
      - Sleuth: distributed tracing via logs with Spring Cloud Sleuth 
      - Spring Boot DevTools
      - Actuator
      - Lombok
   2. Configuration 
      - Enable Zuul Proxy with @EnableZuulProxy 
      - Register to Eureka Server with @EnableDiscoveryClient
[[ZuulApiGatewayServerApplication]()]
      - Configure in 
[[application.properties]()]
        - Application name
        - Port
        - Eureka Server URL
2. Create Filter class extends ZuulFilter with @Component 
[[ZuulLoggingFilter]()]
   - Implement abstract methods:
     - ```filterOrder()```: priority when there are multiple Filters
     - ```shouldFilter()```: excecute the filter for every request or not (invoke the ```run()```)
     - ```run()```: core method of the filter handling bussiness logic
     - ```filterType()```: when to execute the filter
   - Get the current request using RequestContext

3. Setup Zuul API Gateway between Microservices: update Feign Proxy interface 
[[CurrencyExchangeServiceProxy]()]
   - Connect to the Zuul Gateway by @FeignClient instead to other Microservice
   - Update the endpoint mapping of Zuul Gateway: append the Microsevice name (which has the REST API) at the beginning
   
4. Test the Filter
   - Run Eureka Server
   - Run all Microservices
   - Run Zuul Server
   - Access Zuul Gateway URL directly: ```localhost:<zuul_server_port>/<microservice_name/<microservice_endpoint>```
     - E.g: ```http://localhost:8000/currency-exchange/from/BBB/to/CCC``` => ```http://localhost:8765/currency-exchange-microservice/currency-exchange/from/BBB/to/CCC```
   - Access endpoints which invoke REST API from other Microservice through Zuul Gateway

### Spring Cloud Sleuth
- Description: add a unique ID to a request to trace it across multiple Microservices
1. Sleuth dependency
2. Create Sample Bean (brave.sampler) 
[[ZuulApiGatewayServerApplication]()] 
[[CurrencyConverterMicroserviceApplication]()] 
[[CurrencyExchangeMicroserviceApplication]()] 
   

---



### Notes - Tips
- [Spring Cloud] For every change in the Config Git Repo, need to commit and restart the Config Server
- [Spring] ```bootstrap.properties``` used for Spring Cloud has higher priority than ```application.properties``` used in Spring Boot
- [Spring Core] Inject properties for Microservices:
  1. Declare application properties (for injection instead of hard-coding)  
[[application.properties]()]
  2. Create a Configuration class with @ConfigurationProperties and @Component (to read the properties) 
[[Configuration]()] 
     - Add prefix (?)
     - Declare variables matching the needed properties
     - Create setter and getter or use @Data
  3. Inject the Configuration in Controllers using @Autowired (to get the properties in application.properties) 
[[LimitsMicroserviceApplication]()]
- [Spring Core] Prefer injecting properties by create a Configuration class to using @Value
- [Spring] Use @PostConstruct in the Controller to simulate in-memory database for protyping
- [Spring Core] Retrieve application port by injecting Enviroment [springframework.core] 
[[CurrencyExchangeMicroserviceApplication]()]
- [Esclipse] Create multiple instances of Microservice on different ports (set port for application externally):
  1. ```Choose service project -> Run As -> Run Configurations```
  2. Duplicate the service configuration in Java application
  3. Selet Arguments tag, for VM arguments: ```-Dserver.port=<port>```
- [Eureka] Eureka clients use ```jackson-dataformat-xml```. To enable JSON format, exclude xml dependency when adding Eureka Client dependency 
[[pom.xml]()]
- [Spring Cloud] Order of application execution: Name Server -> Microservices -> Zuul API Gateway Server













