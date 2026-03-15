# Spring Boot Boilerplate

Reusable base for REST APIs built with **Java 21 · Spring Boot 3 · JWT · Scopes · MySQL · WebClient**.

---

## Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (JJWT 0.12) |
| Persistence | Spring Data JPA + Hibernate + MySQL |
| HTTP Client | Spring WebFlux `WebClient` |
| Build | Maven |
| Utilities | Lombok |

---

## Package structure

```
com.boilerplate
├── annotation          @ScopeAllowed
├── aspect              ScopeAspect (AOP enforcement)
├── auth                AuthController, AuthService, dto/
├── client              BaseWebClientService, ExternalApiClient
├── config              ApplicationConfig, WebClientConfig
├── exception           GlobalExceptionHandler, exceptions, ErrorResponseDTO
├── filter              JwtAuthenticationFilter
├── scope               Scope (entity), ScopeRepository, ScopeService
├── security            JwtService, SecurityConfig
└── user                User (entity), UserRepository, UserService, UserController, dto/
```

---

## Quick setup

### 1. Environment variables (or `application.yml`)

| Variable | Description | Default |
|---|---|---|
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | `root` |
| `JWT_SECRET` | JWT signing secret (≥ 32 chars) | see yml |

### 2. Database

```sql
CREATE DATABASE boilerplate_db CHARACTER SET utf8mb4;
```

> With `ddl-auto: update`, Hibernate creates the tables automatically.
> `data.sql` seeds the initial scopes on every startup.

### 3. Build and run

```bash
mvn clean package -DskipTests
java -jar target/spring-boot-boilerplate-1.0.0.jar
```

Or directly with Maven:

```bash
mvn spring-boot:run
```

---

## Endpoints

### Authentication (public)

#### Register
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "scopes": ["user.read"]
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "secret123"
}
```

**Response:**
```json
{
  "access_token": "eyJ...",
  "expiration": 86400000,
  "user": { "id": 1, "username": "john", ... }
}
```

#### Current user
```http
GET /auth/me
Authorization: Bearer <token>
```

### Users (protected)

```http
GET /users
Authorization: Bearer <token>
# Requires scope: user.read

GET /users/{id}
Authorization: Bearer <token>
# Requires scope: user.read
```

---

## Scope system

Scopes are stored in the `scopes` table and assigned to users via the `user_scopes` join table.

They are embedded in the JWT payload:
```json
{ "scopes": ["user.read", "user.write"] }
```

### Using `@ScopeAllowed`

```java
@ScopeAllowed({"user.read"})         // user must hold at least one of the listed scopes
@GetMapping("/users")
public List<UserResponseDTO> list() { ... }
```

The `ScopeAspect` intercepts the call and throws `403 Forbidden` if the token does not contain any of the required scopes.

---

## Adding a new external integration

```java
@Component
public class PaymentApiClient extends BaseWebClientService {

    public PaymentApiClient(WebClient.Builder builder,
                            @Value("${payment.api.url}") String baseUrl) {
        super(builder, baseUrl);
    }

    public ChargeResponseDTO charge(ChargeRequestDTO req) {
        return post("/charge", req, ChargeResponseDTO.class);
    }
}
```

---

## Standard error response

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Insufficient scopes. Required one of: [user.read]",
  "path": "/users",
  "timestamp": "2024-01-15T10:30:00",
  "field_errors": null
}
```

---

## Available scopes (seed data)

| Scope | Description |
|---|---|
| `user.read` | Read user data |
| `user.write` | Create and update users |
| `user.delete` | Delete users |
| `admin` | Full administrative access |
