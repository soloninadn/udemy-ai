# Udemy AI - Developer Guidelines

Welcome! This document provides a quick, practical overview to help you get productive fast.

## Tech Stack
- Java 21
- Spring Boot 3.5.5
  - spring-boot-starter-web (REST)
  - spring-boot-starter-data-jpa (JPA/Hibernate)
- H2 Database (runtime, file-less for dev/tests)
- Lombok (code generation for getters/setters, builder)
- JUnit 5 + Spring Test + MockMVC
- Build: Maven

## Project Layout
- src/main/java/ua/ardas/udemy_ai
  - UdemyAiApplication.java — Spring Boot entry point
  - entities/Transaction.java — JPA entity (+ lifecycle hooks)
  - repositories/TransactionRepository.java — Spring Data repository
  - services/
    - TransactionService.java — service API
    - TransactionServiceImpl.java — service implementation
  - controllers/TransactionController.java — REST controller
- src/main/resources/application.properties — basic Spring config
- src/test/java/ua/ardas/udemy_ai
  - UdemyAiApplicationTests.java — context load
  - repositories/TransactionRepositoryTest.java — repository CRUD tests
  - controllers/TransactionControllerTest.java — REST controller tests (MockMVC)
- pom.xml — dependencies & plugins

## Prerequisites
- JDK 21
- Maven 3.9+

Optional IDE plugins: Lombok plugin (if using IntelliJ/IDE) and annotation processing enabled.

## Quick Start
- Build: mvn -q -DskipTests package
- Run: mvn spring-boot:run
- Run tests: mvn -q test

Application will start on http://localhost:8080 by default.

## Runtime & Database
- H2 is included for development/testing; Spring Boot auto-configures an in-memory database.
- The Transaction entity uses IDENTITY strategy; timestamps are set via @PrePersist/@PreUpdate.

## REST API (Transaction)
Base path: /api/transactions
- GET /api/transactions — list all
- GET /api/transactions/{id} — get by ID (404 if not found)
- POST /api/transactions — create new
  - Request body (JSON): { amount, currency, description, status }
  - Response: 201 Created with Location header and created entity

Notes:
- amount is BigDecimal
- currency: ISO-4217 code (e.g., "USD")
- status: PENDING | COMPLETED | FAILED | CANCELED (defaults to PENDING on create if omitted)

## Coding Conventions
- Use Lombok for boilerplate (@Getter, @Setter, @Builder, etc.).
- Keep controller thin; delegate to service; service uses repository.
- Prefer constructor injection (@RequiredArgsConstructor) for components.
- For new endpoints, add unit or slice tests (e.g., @WebMvcTest) and repository tests (@DataJpaTest) as appropriate.

## Testing
- Repository tests: @DataJpaTest (isolated JPA/H2 layer)
- Controller tests: @WebMvcTest with mocked services (see TransactionControllerTest)
- Run: mvn test

## Common Issues & Tips
- Lombok not working in IDE: enable annotation processing and install Lombok plugin.
- H2 console is not enabled by default; if needed, add spring.h2.console.enabled=true and related settings in application.properties.
- JSON serialization for BigDecimal renders numbers; adjust tests using matchers (e.g., closeTo) as shown.

## Adding New Features
1. Define/extend entity in entities/.
2. Add repository interface in repositories/.
3. Implement service API in services/.
4. Expose REST endpoints in controllers/.
5. Write tests for repository and controller layers.

## Contact & Maintenance
- Java version is pinned in pom.xml (<java.version>21</java.version>).
- Spring Boot version: 3.5.5 (verify compatibility when upgrading dependencies).
