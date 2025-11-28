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

### Build and run with Docker
You can build a production-ready container image with Java 21 using the provided `Dockerfile`.

```bash
docker build -t aiadmin-backend .
```

Run the container (point it at your database and provide the API key):

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/aiadmin" \
  -e SPRING_DATASOURCE_USERNAME=aiadmin \
  -e SPRING_DATASOURCE_PASSWORD=aiadmin \
  -e SECURITY_API_KEY=change-me \
  aiadmin-backend
```

### PostgreSQL container image
You do **not** need a separate Dockerfile for PostgreSQL: the included `docker-compose.yml` already uses the official `postgres:16` image with the `aiadmin` database, user, and password exposed on port `5432`. Use that image as-is unless you need custom extensions or initialization, in which case you can swap the image in `docker-compose.yml` for your own.

## Project structure
- `src/main/java/com/nejat/projects/aiadmin/controller` – REST controllers for HTTP endpoints.
- `src/main/java/com/nejat/projects/aiadmin/service` – business logic services invoked by controllers.
- `src/main/java/com/nejat/projects/aiadmin/repository` – Spring Data JPA repositories for persistence access.
- `src/main/java/com/nejat/projects/aiadmin/model` – JPA entities and supporting domain types.
- `src/main/resources` – application configuration (e.g., `application.properties`).

## Configuration
Default datasource settings live in `src/main/resources/application.properties` and match the Docker Compose service. You can override them via environment variables when running the app (for example `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`).

### OpenAI
Configure OpenAI access for the LLM client with the following `application.yml` snippet (environment variables recommended for secrets):

```yaml
aiadmin:
  openai:
    apiKey: ${OPENAI_API_KEY:}
    model: gpt-4.1-mini
    baseUrl: https://api.openai.com/v1
```

### Security
- Requests must include an `X-API-KEY` header that matches `security.api-key` (configure via `SECURITY_API_KEY` environment variable or in `application.properties`).
- CORS is restricted to the Personal Assistant Agent frontend (`https://soleymantic.github.io/personal-assistant-agent`), its GitHub Pages root, and common local dev origins. Customize the origins with the `security.allowed-origins` property if needed.
- Security headers and stateless sessions are enforced through Spring Security to harden the REST API surface.

## Development tips
- Use `mvn test` to run the Maven test suite.
- Lombok is included; ensure your IDE supports it for the best developer experience.
- Keep configuration and documentation in sync when changing database credentials or connection details.
