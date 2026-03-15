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
├── discord             DiscordClient, DiscordService, dto/
├── exception           GlobalExceptionHandler, exceptions, ErrorResponseDTO
├── filter              JwtAuthenticationFilter
├── home                HomeController, dto/
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

### Home (protected)

```http
GET /home/echo?message=hello
Authorization: Bearer <token>
# Requires scope: home.echo
```

**Response:**
```json
{
  "echo": "hello",
  "received_from": "john",
  "timestamp": "2024-01-15T10:30:00"
}
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

## Discord integration

Sends messages to a Discord channel via an incoming webhook.

### Configuration

```yaml
app:
  discord:
    webhook-url: ${DISCORD_WEBHOOK_URL:https://discord.com/api/webhooks/YOUR_ID/YOUR_TOKEN}
    username: ${DISCORD_USERNAME:Boilerplate Bot}
```

> To get a webhook URL: Discord server settings → Integrations → Webhooks → New Webhook → Copy URL.

### Usage

```java
// Plain text
discordService.sendSimpleMessage("Server started!");

// Rich embed
discordService.sendEmbed(DiscordEmbedDTO.builder()
    .title("New user registered")
    .description("john@example.com just signed up")
    .color(5814783)   // #58B9FF
    .build());

// Embed with fields
discordService.sendInfoEmbed("Deployment complete", 3066993,
    DiscordEmbedFieldDTO.builder().name("Version").value("1.2.0").inline(true).build(),
    DiscordEmbedFieldDTO.builder().name("Env").value("Production").inline(true).build()
);
```

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
| `home.echo` | Access the echo endpoint |

---

## Tests

Integration tests run against an in-memory H2 database (MySQL compatibility mode) — your production MySQL is never touched.

```bash
mvn test
```

| Test | Description |
|---|---|
| `register_shouldReturn201WithToken` | Register returns 201 + JWT |
| `login_shouldReturn200WithToken` | Login returns 200 + JWT |
| `login_shouldReturn401OnWrongPassword` | Wrong password returns 401 |
| `echo_shouldReturn200WithEchoedMessage` | Echo endpoint returns message + username |
| `echo_shouldReturn403WithoutToken` | No token returns 403 |
| `echo_shouldReturn403WithoutRequiredScope` | Missing scope returns 403 |
| `fullFlow_registerLoginAndConsumeEcho` | Full register → login → echo flow |
