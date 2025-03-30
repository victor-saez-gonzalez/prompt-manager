# Prompt Manager 🧠

**Prompt Manager** is a web application for managing personalized prompts, categories, and tags, with support for authenticated users. The goal is to provide a solid foundation for a system to organize, reuse, and classify prompts for LLMs.

## ✨ Features

- RESTful API with Spring Boot 3
- Basic Authentication with Spring Security
- Interactive API documentation with Swagger UI (`springdoc-openapi`)
- Management of:
  - Categories
  - Tags
  - Prompts
  - Users
- Pagination and sorting in endpoints
- Configuration with profiles (`dev`)
- H2 console enabled for development
- Ready for future integration with a React frontend

## 🚀 Technologies

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- H2 Database
- Lombok
- Swagger / OpenAPI (`springdoc-openapi`)
- Maven (multi-module)
- GitHub Actions (coming soon)
- Google Cloud Platform (future deployment)

## 🏗️ Project Structure

```bash
prompt-manager/
├── prompt-manager-model/        # Domain models
├── prompt-manager-common/       # DTOs, mappers, shared classes
├── prompt-manager-persistence/  # Repositories and JPA entities
├── prompt-manager-service/      # Business logic
├── prompt-manager-web/          # Controllers, configuration and entrypoint
```

## 🔐 Authentication

Currently uses basic authentication (`Basic Auth`) in the `dev` profile, with an in-memory user:

- **Username:** `admin`
- **Password:** `admin123`

## 📖 API Documentation

Access Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

## 🔧 Configuration

`application.yml` includes `dev` profile, H2 database and console available at:

```
http://localhost:8080/h2-console
```

## 🩺 Actuator

Health and metrics endpoints available at:

```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
```

> ⚠️ In the `dev` profile, these endpoints are exposed without authentication.

## ✅ Next Steps

- Implement the `User` entity with types and roles
- Complete relationships between `Prompt`, `Tag`, and `Category`
- Add role-based access control
- Integrate frontend in React
- Deploy to Google Cloud Run
