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
 
## Flow:
![Auth Server Flows](https://github.com/user-attachments/assets/29e5ed90-68a3-4ee1-8615-99447e564c83)

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
- 
## Kafka - Events Flow
![Untitled diagram-2024-12-19-194632](https://github.com/user-attachments/assets/4aabe01f-3727-458d-b554-71082c374a20)

```
graph TD
    %% Main Components
    AuthService[AuthService]
    KafkaProducer[Kafka Producer]
    UserEventsTopic["<b>user-events Topic</b><br>Partitions: 1"]
    AuthEventsTopic["<b>auth-events Topic</b><br>Partitions: 1"]

    %% User Registration Event
    AuthService -->|"userCreatedEvent<br><b>Key</b>: userId<br><b>Partition:</b> userId mod 3"| KafkaProducer
    KafkaProducer -->|"Publishes to"| UserEventsTopic
    UserEventsTopic -->|"Acknowledged"| KafkaProducer

    %% Successful Authentication Event
    AuthService -->|"userAuthenticatedEvent<br><b>Key:</b> userId<br><b>Partition:</b> 0"| KafkaProducer
    KafkaProducer -->|"Publishes to"| AuthEventsTopic
    AuthEventsTopic -->|"Acknowledged"| KafkaProducer

    %% Failed Login Event
    AuthService -->|"loginFailedEvent<br><b>Key:</b> email<br><b>Partition:</b> 0"| KafkaProducer
    KafkaProducer -->|"Publishes to"| AuthEventsTopic
    AuthEventsTopic -->|"Acknowledged"| KafkaProducer
```

## Testing Messaging:
### Topic Messages: auth-events
![image](https://github.com/user-attachments/assets/29e49642-55d9-4484-b5b0-94b977113dee)
### Topic Messages: user-events
![image](https://github.com/user-attachments/assets/3843af38-4b86-434e-bcd7-fe705a577b77)

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
