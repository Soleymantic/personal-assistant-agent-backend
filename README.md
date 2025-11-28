# Personal Assistant Agent Backend

A Spring Boot 3.3 starter project for a personal assistant/AI admin backend. The repository currently provides the application scaffolding, PostgreSQL configuration, and Docker support so you can focus on adding your own domain logic.

## Requirements
- Java 21
- Maven 3.9+
- Docker (optional, for the local PostgreSQL service)

## Quick start
1. Start the database (optional if you already have PostgreSQL available):
   ```bash
   docker-compose up -d
   ```
   The local database uses the `aiadmin` database, user, and password exposed on port `5432`.
2. Launch the application:
   ```bash
   mvn spring-boot:run
   ```

## Project structure
- `src/main/java/com/nejat/projects/aiadmin/controller` – REST controllers for HTTP endpoints.
- `src/main/java/com/nejat/projects/aiadmin/service` – business logic services invoked by controllers.
- `src/main/java/com/nejat/projects/aiadmin/repository` – Spring Data JPA repositories for persistence access.
- `src/main/java/com/nejat/projects/aiadmin/model` – JPA entities and supporting domain types.
- `src/main/resources` – application configuration (e.g., `application.properties`).

## Configuration
Default datasource settings live in `src/main/resources/application.properties` and match the Docker Compose service. You can override them via environment variables when running the app (for example `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`).

## Development tips
- Use `mvn test` to run the Maven test suite.
- Lombok is included; ensure your IDE supports it for the best developer experience.
- Keep configuration and documentation in sync when changing database credentials or connection details.
