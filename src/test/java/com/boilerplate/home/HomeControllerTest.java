package com.boilerplate.home;

import com.boilerplate.auth.dto.AuthResponseDTO;
import com.boilerplate.auth.dto.LoginRequestDTO;
import com.boilerplate.auth.dto.RegisterRequestDTO;
import com.boilerplate.home.dto.EchoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HomeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String USERNAME = "echouser";
    private static final String EMAIL    = "echouser@example.com";
    private static final String PASSWORD = "password123";

    // Ensures the user exists before each test (idempotent — server returns 400 if duplicate)
    @BeforeEach
    void ensureUserExists() {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .scopes(Set.of("home.echo"))
                .build();
        restTemplate.postForEntity("/auth/register", request, Void.class);
    }

    // ── 1. Register ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /auth/register → 201 and returns JWT")
    void register_shouldReturn201WithToken() {
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("newpassword1")
                .scopes(Set.of("home.echo"))
                .build();

        ResponseEntity<AuthResponseDTO> response =
                restTemplate.postForEntity("/auth/register", request, AuthResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("newuser");
        assertThat(response.getBody().getUser().getScopes()).contains("home.echo");
    }

    // ── 2. Login ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /auth/login → 200 and returns JWT")
    void login_shouldReturn200WithToken() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();

        ResponseEntity<AuthResponseDTO> response =
                restTemplate.postForEntity("/auth/login", request, AuthResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
    }

    @Test
    @DisplayName("POST /auth/login with wrong password → 401")
    void login_shouldReturn401OnWrongPassword() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username(USERNAME)
                .password("wrongpassword")
                .build();

        ResponseEntity<Void> response =
                restTemplate.postForEntity("/auth/login", request, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── 3. GET /home/echo ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /home/echo → 200 and echoes the message")
    void echo_shouldReturn200WithEchoedMessage() {
        String token = loginAndGetToken();

        ResponseEntity<EchoResponseDTO> response = restTemplate.exchange(
                "/home/echo?message=hello",
                HttpMethod.GET,
                bearerRequest(token),
                EchoResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEcho()).isEqualTo("hello");
        assertThat(response.getBody().getReceivedFrom()).isEqualTo(USERNAME);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("GET /home/echo without token → 403")
    void echo_shouldReturn403WithoutToken() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/home/echo?message=hello",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("GET /home/echo with token missing scope → 403")
    void echo_shouldReturn403WithoutRequiredScope() {
        // Register a user with no scopes
        RegisterRequestDTO request = RegisterRequestDTO.builder()
                .username("noscopeuser")
                .email("noscope@example.com")
                .password("password123")
                .build();
        ResponseEntity<AuthResponseDTO> registerResponse =
                restTemplate.postForEntity("/auth/register", request, AuthResponseDTO.class);

        String token = registerResponse.getBody().getAccessToken();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/home/echo?message=hello",
                HttpMethod.GET,
                bearerRequest(token),
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ── Full flow ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Full flow: register → login → echo")
    void fullFlow_registerLoginAndConsumeEcho() {
        // 1. Register
        RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
                .username("flowuser")
                .email("flowuser@example.com")
                .password("flowpass123")
                .scopes(Set.of("home.echo"))
                .build();

        ResponseEntity<AuthResponseDTO> registerResponse =
                restTemplate.postForEntity("/auth/register", registerRequest, AuthResponseDTO.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody().getAccessToken()).isNotBlank();

        // 2. Login
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .username("flowuser")
                .password("flowpass123")
                .build();

        ResponseEntity<AuthResponseDTO> loginResponse =
                restTemplate.postForEntity("/auth/login", loginRequest, AuthResponseDTO.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResponse.getBody().getAccessToken();
        assertThat(token).isNotBlank();

        // 3. Call protected endpoint
        ResponseEntity<EchoResponseDTO> echoResponse = restTemplate.exchange(
                "/home/echo?message=integration-test",
                HttpMethod.GET,
                bearerRequest(token),
                EchoResponseDTO.class);

        assertThat(echoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(echoResponse.getBody().getEcho()).isEqualTo("integration-test");
        assertThat(echoResponse.getBody().getReceivedFrom()).isEqualTo("flowuser");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String loginAndGetToken() {
        LoginRequestDTO request = LoginRequestDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        AuthResponseDTO body = restTemplate
                .postForEntity("/auth/login", request, AuthResponseDTO.class)
                .getBody();
        assertThat(body).isNotNull();
        return body.getAccessToken();
    }

    private HttpEntity<Void> bearerRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
}
