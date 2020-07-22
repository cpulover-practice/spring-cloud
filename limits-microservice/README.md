# Limits Microservice

### Environment Setup
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

### Properties Injection
1. Declare application properties (for injection instead of hard-coding)  
[[application.properties]()]
2. Create a Configuration class with @ConfigurationProperties and @Component (to read the properties) 
[[Configuration]()] 
   - Add prefix (?)
   - Declare variables matching the needed properties
   - Create setter and getter or use @Data
3. Inject the Configuration in Controllers using @Autowired (to get the properties in application.properties) 
[[LimitsMicroserviceApplication]()]

### Notes - Tips
- Prefer injecting properties by create a Configuration class to using @Value