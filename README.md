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
└── prompt-manager-web/          # Controllers, configuration and entrypoint
```

## 🔐 Authentication

Currently uses basic authentication (`Basic Auth`) in the `dev` profile, with an in-memory user:

- **Username:** `admin`
- **Password:** `your_admin_password`

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

## 🧪 Running the Application

### 🔁 With Spring Boot and IntelliJ (or CLI)

You can run the application using different profiles by passing the `spring.profiles.active` parameter.

#### ▶ Development mode (in-memory H2 database)

```bash
-Dspring.profiles.active=dev
```

#### ▶ Launching from Command Line

Navigate to the `prompt-manager-web` module and run:

```bash
cd prompt-manager-web
mvn "-Dspring-boot.run.profiles=dev" spring-boot:run
```

This will start the application using the `dev` profile.



This uses:

- H2 in-memory database
- Swagger UI and H2 Console enabled
- Basic Auth (`admin` / `admin123`)

#### ▶ Docker profile (PostgreSQL with Docker)

Make sure to launch the PostgreSQL container first:

```bash
docker-compose up -d
```

Then run the application with the `docker` profile:

```bash
mvn "-Dspring-boot.run.profiles=docker" spring-boot:run
```

This connects to the local PostgreSQL database defined in `docker-compose.yml` and configured via environment variables in the `.env` file.

Default connection values (from `.env`):

- **Host:** `localhost`
- **Port:** `5433` (or the value you set in `.env`)
- **Database:** `your_database`
- **User:** `your_user`
- **Password:** `your_password`

> ✅ If you're using environment variables in `application-docker.yml`, make sure they match your local `.env` configuration or are set properly in your environment or IntelliJ run configuration.


## 🐳 Docker Setup for PostgreSQL

To run the PostgreSQL database locally using Docker Compose:

1. Ensure you have Docker and Docker Compose installed.
2. Create a `.env` file in the root directory based on the provided `.env.example`.
  - The `.env` file contains sensitive configuration like database name, username, password, and port.
  - **Do not commit the `.env` file to GitHub.**
3. Example of `.env`:

   ```env
   POSTGRES_USER=your_user
   POSTGRES_PASSWORD=your_password
   POSTGRES_DB=your_database
   POSTGRES_PORT=5433
   ```

4. Launch the PostgreSQL container:

   ```bash
   docker-compose up -d
   ```

5. Verify that PostgreSQL is running:

   ```bash
   docker ps
   ```

The database will be available at:

- **Host:** `localhost`
- **Port:** as defined in `POSTGRES_PORT` (default `5433`)
- **Database name:** as defined in `POSTGRES_DB`
- **Username:** as defined in `POSTGRES_USER`
- **Password:** as defined in `POSTGRES_PASSWORD`

> ℹ️ The `.env.example` file is included in the repository to help you set up your own `.env` file easily.
> Always make sure `.env` is listed in your `.gitignore` file to protect sensitive information.


### 🧪 Test the connection

- Open [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui) after running the app with the `docker` profile.
- Confirm that your backend is connected to PostgreSQL.

---

## 🐳 Running the Backend with Docker

### 🏗️ 1. Build the Docker image

Make sure you're in the root folder of the project (where the `Dockerfile` is located):

```bash
docker build -t prompt-manager-backend .
```

This will create a Docker image named `prompt-manager-backend`.

### 🚀 2. Run the container connected to your PostgreSQL instance

If you're using PostgreSQL via Docker (`docker-compose`), the database is exposed on your **host machine** (e.g. `localhost:5433`).

However, from inside the container, `localhost` refers to itself, so we must use your actual host IP address.

#### 🔍 Get your local IP address

On Windows PowerShell or CMD:

```bash
ipconfig
```

Look for something like:

```
IPv4 Address. . . . . . . . . . . : 192.168.1.42
```

### ▶ Run the backend container

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.1.42:5433/your_database \
  -e SPRING_DATASOURCE_USERNAME=your_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  prompt-manager-backend
```

> Replace `192.168.1.42` with your actual IP address.

This will:

- Start the backend container
- Connect it to the running PostgreSQL container (on your host)
- Expose the app at [http://localhost:8080](http://localhost:8080)

### 🧪 Test it

Open your browser and go to:

```
http://localhost:8080/swagger-ui/index.html
```

You should be able to interact with your API using real PostgreSQL storage.
