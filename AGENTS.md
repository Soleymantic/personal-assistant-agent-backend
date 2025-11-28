# Agent Guidelines

These instructions apply to the entire repository.

- Keep the README up to date with any changes to configuration, dependencies, or how to run the service (including Docker and database settings).
- The project targets Java 21 and Spring Boot 3.3; align new code and dependencies with those versions.
- Use Maven commands (e.g., `mvn test`, `mvn spring-boot:run`) for building, running, and verifying changes where applicable.
- Avoid committing secrets or real credentials; prefer environment variables and sample values in configuration files.
- When modifying database connection details, update both `docker-compose.yml` and `src/main/resources/application.properties` to stay consistent.
- Maintain a clean, layered package structure (controller → service → repository → model) and keep new code organized accordingly; the user values tidy, professional work.
