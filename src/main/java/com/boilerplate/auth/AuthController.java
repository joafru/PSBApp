package com.boilerplate.auth;

import com.boilerplate.auth.dto.AuthResponseDTO;
import com.boilerplate.auth.dto.LoginRequestDTO;
import com.boilerplate.auth.dto.RegisterRequestDTO;
import com.boilerplate.user.User;
import com.boilerplate.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** POST /auth/register — create a new account */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return authService.register(request);
    }

    /** POST /auth/login — authenticate and receive JWT */
    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /** GET /auth/me — return the currently authenticated user (requires valid JWT) */
    @GetMapping("/me")
    public UserResponseDTO me(@AuthenticationPrincipal User user) {
        return UserResponseDTO.from(user);
    }
}
