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