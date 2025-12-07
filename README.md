# Authentication Service

Spring Boot 3.3 backend providing JWT authentication, refresh tokens, and Google Sign-In integration.

## Requirements
- Java 21
- Maven 3.9+
- Docker (optional for local PostgreSQL)

## Configuration
All secrets/credentials should be provided through environment variables.

### Application
`src/main/resources/application.yml` (and mirrored `application.properties`) read the following variables:

- `DB_URL` (default `jdbc:postgresql://localhost:5432/aiadmin`)
- `DB_USERNAME` (default `aiadmin`)
- `DB_PASSWORD` (default `aiadmin`)
- `JWT_SECRET` (HMAC secret for signing tokens)
- `JWT_EXPIRATION` (access token validity in milliseconds, default 900000)
- `JWT_REFRESH_EXPIRATION` (refresh token validity in milliseconds, default 2592000000)
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `GOOGLE_REDIRECT_URI` (default `{baseUrl}/login/oauth2/code/google`)

### Docker Compose database
The included PostgreSQL service exposes the `aiadmin` database, user, and password on port `5432`:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16
    restart: unless-stopped
    environment:
      POSTGRES_DB: aiadmin
      POSTGRES_USER: aiadmin
      POSTGRES_PASSWORD: aiadmin
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
volumes:
  postgres_data:
```

## Running the application

```bash
mvn spring-boot:run
```

Or build and run the container image:

```bash
docker build -t auth-service .
docker run --rm -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/aiadmin" \
  -e DB_USERNAME=aiadmin \
  -e DB_PASSWORD=aiadmin \
  -e JWT_SECRET=change-me \
  -e GOOGLE_CLIENT_ID=your-client-id \
  -e GOOGLE_CLIENT_SECRET=your-client-secret \
  auth-service
```

## REST API
- `POST /api/auth/register` – email/password registration (creates `ROLE_USER` with `authProvider=LOCAL`).
- `POST /api/auth/login` – returns `accessToken`, `refreshToken`, and user data for valid credentials.
- `POST /api/auth/refresh` – exchanges a valid refresh token for new tokens.
- `GET /api/user/me` – returns the authenticated user's profile (JWT required).
- `GET /api/admin/users` – admin-only user listing (`ROLE_ADMIN`).

### Google Sign-In
Initiate OAuth2 via `/oauth2/authorization/google`. On success the backend returns JSON (no redirect) containing access/refresh tokens and the Google user profile; new users are created automatically with `authProvider=GOOGLE` and `ROLE_USER`.

## Project structure
- `com.nejat.projects` – Spring Boot entrypoint.
- `com.nejat.projects.config` – security configuration.
- `com.nejat.projects.security.jwt` – JWT provider and filter.
- `com.nejat.projects.security.oauth2` – Google OAuth2 integration.
- `com.nejat.projects.auth` – authentication controllers/services.
- `com.nejat.projects.user` – user domain, repository, controller.
- `com.nejat.projects.dto` – request/response DTOs and mappers.
- `com.nejat.projects.exception` – exception model and global handler.
