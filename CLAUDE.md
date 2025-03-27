# B2B Commerce Project Guide

## Build/Test Commands

- Build project: `./gradlew build`
- Run tests: `./gradlew test`
- Single test: `./gradlew test --tests "app.quantun.b2b.TestClassName.testMethodName"`
- Skip tests: `./gradlew build -x test`
- Run app: `./gradlew bootRun`
- Docker build: `docker build -t b2bcommerce .` or `docker-compose up -d`

## Code Style Guidelines

- Java 17, Spring Boot 3.4.x
- Max line length: 120 characters
- Indentation: 4 spaces, no tabs
- Naming: camelCase for variables/methods, PascalCase for classes, UPPER_SNAKE_CASE for constants
- Use Jakarta EE (jakarta.*) not legacy javax.* imports
- No wildcard imports or unused imports
- Follow existing patterns for entity classes, services, controllers
- Use JPA annotations consistently (@Entity, @Column, etc.)
- Error handling: Use custom exceptions or ProblemDetail for standardized error responses
- Organize imports alphabetically
- Entity classes should include proper equals/hashCode using entity ID
- Document public methods with Javadoc
- Use Lombok annotations consistently (@Getter, @Setter, etc.)

## Technical Decisions

### Technology Stack

- **Java 17**: Chosen for its long-term support and modern language features.
- **Spring Boot 3.4.x**: Provides a comprehensive framework for building enterprise applications with minimal configuration.
- **Hibernate**: Used for ORM to simplify database interactions.
- **Liquibase**: Manages database schema changes in a controlled manner.
- **Docker**: Containerizes the application for consistent deployment across environments.
- **Redis**: Used for caching to improve application performance.
- **AWS Cognito**: Provides secure user authentication and authorization.

### Configuration Choices

- **application.properties**: Centralized configuration file for managing application settings.
- **ModelMapper**: Simplifies object mapping between DTOs and entities.
- **OpenAPI**: Generates API documentation for better developer experience.
- **SecurityConfig**: Customizes security settings to meet application requirements.
- **GlobalExceptionHandler**: Standardizes error handling across the application.

### Dependency Management

- **Gradle**: Chosen for its flexibility and performance in managing project dependencies and build tasks.
- **Spring Boot Starters**: Simplifies dependency management by providing pre-configured sets of dependencies for common use cases.
- **JUnit**: Used for unit testing to ensure code quality and reliability.
- **Mockito**: Facilitates mocking in tests to isolate components and test behavior.
- **Cucumber**: Supports behavior-driven development (BDD) by allowing tests to be written in a human-readable format.

### Design Patterns

- **Singleton**: Ensures a single instance of certain classes, such as configuration classes.
- **Factory**: Used to create instances of complex objects, such as AWS clients.
- **Builder**: Simplifies the creation of complex objects, such as entities with many fields.
- **Strategy**: Encapsulates algorithms, such as different authentication strategies, to make them interchangeable.

### Best Practices

- **Code Reviews**: Regular code reviews to maintain code quality and share knowledge among team members.
- **Continuous Integration**: Automated builds and tests to catch issues early in the development process.
- **Documentation**: Comprehensive documentation to ensure that the codebase is understandable and maintainable.
- **Security**: Regular security audits and updates to protect against vulnerabilities.
