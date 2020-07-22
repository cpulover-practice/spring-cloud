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
   2. Configure git URL in Server properties (use ```/``` instead of ```\```) 
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


### Notes - Tips
- For every change in the Config Git Repo, need to commit and restart the Config Server
- ```bootstrap.properties``` used for Spring Cloud has higher priority than ```application.properties``` used in Spring Boot

















