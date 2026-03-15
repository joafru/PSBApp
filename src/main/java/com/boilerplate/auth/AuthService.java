package com.boilerplate.auth;

import com.boilerplate.auth.dto.AuthResponseDTO;
import com.boilerplate.auth.dto.LoginRequestDTO;
import com.boilerplate.auth.dto.RegisterRequestDTO;
import com.boilerplate.exception.UnauthorizedException;
import com.boilerplate.scope.Scope;
import com.boilerplate.scope.ScopeService;
import com.boilerplate.security.JwtService;
import com.boilerplate.user.User;
import com.boilerplate.user.UserRepository;
import com.boilerplate.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ScopeService scopeService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Set<Scope> scopes = new HashSet<>();
        if (request.getScopes() != null && !request.getScopes().isEmpty()) {
            scopes = scopeService.findByNames(request.getScopes());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .scopes(scopes)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid credentials");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        return AuthResponseDTO.builder()
                .accessToken(jwtService.generateToken(user))
                .expiration(jwtService.getExpirationTime())
                .user(UserResponseDTO.from(user))
                .build();
    }
}
