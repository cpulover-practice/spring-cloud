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

2. Setup Spring Cloud Config Server 
[[spring-cloud-config-server]()]
   1. Spring Boot dependencies:
      - Spring Config Server
      - Spring Boot DevTools
   2. Configuration 
[[application.properties]()]
      - Application name
      - Port
   3. Enable Spring Cloud Config Server with @EnableConfigServer 
[[SpringCloudConfigServerApplication]()]

3. Create git repository to store configurations of microservices 
[[config-git-repo]()]

4. Connect Config Server to Config Git Repo 
   1. Link to the Repo: ```Choose Config Server project -> Build Path  -> Source -> Link Source -> Choose Config Git folder```
   2. Configure git URL in Server properties (use ```/``` instead of ```\```) 
[[application.properties]()]

5. Create property file for microservices inside the Config Git Repo 
[[limits-microservice.properties]()] [[limits-microservice-dev.properties]()] [[limits-microservice-qa.properties]()]
   - File naming: ```<microservice_name>-profile.properties```
   - Commit the file

6. Test the connection between Config Server and Config Git Repo:
   1. Run Java application for the Config Server
   2. Access URL: ```localhost:<server_port>/<microservice_name>/<profile>



### Notes - Tips
- Need to commit the Config Git Repo for every change


















