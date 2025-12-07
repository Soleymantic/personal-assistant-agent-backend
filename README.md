# Personal Assistant Agent Backend

A Spring Boot 3.3 backend for the Personal Assistant Agent. The project now bundles the original AI admin APIs (secured via `X-API-KEY`) and a new authentication service providing JWT access/refresh tokens and Google Sign-In.

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

Run the container (point it at your database, provide the API key, and supply JWT/OAuth2 settings):

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/aiadmin" \
  -e SPRING_DATASOURCE_USERNAME=aiadmin \
  -e SPRING_DATASOURCE_PASSWORD=aiadmin \
  -e SECURITY_API_KEY=change-me \
  -e JWT_SECRET=change-me-change-me-change-me-32 \
  -e GOOGLE_CLIENT_ID=your-client-id \
  -e GOOGLE_CLIENT_SECRET=your-client-secret \
  aiadmin-backend
```

### PostgreSQL container image
You do **not** need a separate Dockerfile for PostgreSQL: the included `docker-compose.yml` already uses the official `postgres:16` image with the `aiadmin` database, user, and password exposed on port `5432`. Use that image as-is unless you need custom extensions or initialization, in which case you can swap the image in `docker-compose.yml` for your own.

## Project structure
- `src/main/java/com/nejat/projects/aiadmin/controller` – REST controllers for AI admin HTTP endpoints.
- `src/main/java/com/nejat/projects/aiadmin/service` – business logic services invoked by controllers.
- `src/main/java/com/nejat/projects/aiadmin/repository` – Spring Data JPA repositories for persistence access.
- `src/main/java/com/nejat/projects/aiadmin/model` – JPA entities and supporting domain types.
- `src/main/java/com/nejat/projects/auth` – authentication controllers and services.
- `src/main/java/com/nejat/projects/security` – JWT filter/provider and Google OAuth2 integration.
- `src/main/java/com/nejat/projects/user` – user domain, repository, and user-facing endpoints.
- `src/main/resources` – application configuration (e.g., `application.yml`).

## Configuration
Default datasource settings live in `src/main/resources/application.yml` and match the Docker Compose service. You can override them via environment variables when running the app (for example `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`).

### OpenAI
Configure OpenAI access for the LLM client with the following `application.yml` snippet (environment variables recommended for secrets):

```yaml
aiadmin:
  openai:
    apiKey: ${OPENAI_API_KEY:}
    model: gpt-4.1-mini
    baseUrl: https://api.openai.com/v1
```

### Security (API key)
- Requests to the existing AI admin endpoints must include an `X-API-KEY` header that matches `security.api-key` (configure via `SECURITY_API_KEY` environment variable or in `application.properties`).
- CORS is restricted to the Personal Assistant Agent frontend (`https://soleymantic.github.io/personal-assistant-agent`), its GitHub Pages root, and common local dev origins. Customize the origins with the `security.allowed-origins` property if needed.
- Security headers and stateless sessions are enforced through Spring Security to harden the REST API surface.

### Authentication service (JWT + Google Sign-In)
- **Registration/Login**: `/api/auth/register` and `/api/auth/login` issue access/refresh JWTs for local accounts.
- **Refresh**: `/api/auth/refresh` exchanges a valid refresh token for new tokens.
- **Current user**: `/api/user/me` returns the authenticated profile; `/api/admin/users` lists all users for admins.
- **Google Sign-In**: start the OAuth2 flow via `/oauth2/authorization/google`; on success the backend responds with JSON containing tokens and the user profile (no HTML redirect required).
- Configure JWT and Google credentials via environment variables:
  - `JWT_SECRET`, `JWT_EXPIRATION`, `JWT_REFRESH_EXPIRATION`
  - `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_REDIRECT_URI`

## Development tips
- Use `mvn test` to run the Maven test suite.
- Lombok is included; ensure your IDE supports it for the best developer experience.
- Keep configuration and documentation in sync when changing database credentials or connection details.
