# AuthService Microservice

## Overview

AuthService is a reactive Java-based authentication and user management service built with Spring Boot. It provides user registration, login functionality, and integration with other services via events. This service is designed to handle authentication securely using JWT (JSON Web Tokens), password hashing, and reactive programming with Project Reactor.

## Features

- **User Registration**: Allows new users to register with a username, email, and password.
- **Login**: Authenticates users and provides JWT tokens for session management.
- **Reactive Programming**: Fully non-blocking with reactive streams for scalability.
- **Event-Driven Architecture**: Publishes authentication-related events (e.g., user creation, login) via Kafka.
- **Security**: Implements password hashing, token expiration, and role-based access control.

## Technologies Used

### Frameworks and Libraries
- **Spring Boot**: Core framework for creating microservices.
- **Spring WebFlux**: For building reactive REST APIs.
- **Spring Security**: For secure authentication and authorization.
- **Spring Data R2DBC**: For reactive database interaction.
- **Java JWT**: For creating and validating JWT tokens.
- **MapStruct**: For object mapping. (Pending...)
- **Project Reactor**: For handling reactive streams.

### Observability
- Micrometer, OpenTelemetry for tracing and metrics.

### Messaging
- **Kafka**: For event-driven communication.

### Database
- PostgreSQL with Flyway for database migration.
- Redis for caching and token storage.

## Installation

### Prerequisites

- Java 21
- Gradle
- PostgreSQL
- Redis
- Kafka (for event messaging)

### Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/j0rgel0/ms_authservice
   cd authservice
   ```

2. Build the application using Gradle:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. The service will be available at `http://localhost:8080`.

## API Endpoints

### Base URL

`/api/v1/auth`

### Endpoints

#### 1. **Register User**
- **POST** `/register`
- **Description**: Registers a new user.
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "password": "securePassword123"
  }
  ```
- **Response**:
    - `200 OK` with the created user object.
    - `400 Bad Request` if the username or email already exists.

#### 2. **Login**
- **POST** `/login`
- **Description**: Authenticates a user and provides a JWT token.
- **Request Body**:
  ```json
  {
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }
  ```
- **Response**:
    - `200 OK` with a JWT token.
    - `401 Unauthorized` for invalid credentials.

## Project Structure

```plaintext
src
├── main
│   ├── java/com/lox/authservice
│   │   ├── controllers       # REST API controllers
│   │   ├── models            # Data models
│   │   ├── repositories      # Reactive repositories
│   │   ├── services          # Business logic and service layer
│   │   ├── kafka             # Kafka producer and event handling
│   │   └── util              # Utility classes (e.g., JWT utilities)
│   └── resources
│       ├── application.yml   # Configuration
│       └── db/migration      # Flyway migration scripts
└── test
    └── java/com/lox/authservice
        └── tests             # Unit and integration tests
```

## Dependencies

Dependencies are grouped in the `build.gradle` file. Notable ones include:

- Spring Boot starters (WebFlux, Data R2DBC, Security, etc.)
- Redis, PostgreSQL
- Java JWT
- Project Reactor Kafka
- Micrometer for observability

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
