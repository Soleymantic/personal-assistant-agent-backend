# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Maven descriptor first to leverage layer caching
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copy source and build the application
COPY src ./src
RUN mvn -B -DskipTests clean package

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built Spring Boot fat jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
