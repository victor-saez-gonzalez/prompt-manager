# Dockerfile

# --- Build stage ---
FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -B clean package -DskipTests

# --- Final image ---
FROM debian:bookworm-slim

# Install PostgreSQL and required packages
RUN apt-get update && \
    apt-get install -y postgresql postgresql-contrib openjdk-17-jdk && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV POSTGRES_DB=your_database \
    POSTGRES_USER=your_user \
    POSTGRES_PASSWORD=your_password \
    SPRING_PROFILES_ACTIVE=docker

# Prepare PostgreSQL data directory
RUN mkdir -p /var/lib/postgresql/data && chown -R postgres:postgres /var/lib/postgresql
USER postgres
RUN /usr/lib/postgresql/15/bin/initdb -D /var/lib/postgresql/data
USER root

# Copy Spring Boot app
COPY --from=builder /app/prompt-manager-web/target/prompt-manager-web-1.0-SNAPSHOT.jar /app/app.jar

# Copy and set entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Expose application port
EXPOSE 8080

# Entrypoint script runs PostgreSQL and Spring Boot app
CMD ["/entrypoint.sh"]
