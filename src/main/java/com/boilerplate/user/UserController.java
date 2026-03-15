package com.boilerplate.user;

import com.boilerplate.annotation.ScopeAllowed;
import com.boilerplate.user.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Example of a fully protected controller using {@code @ScopeAllowed}.
 *
 * <p>Both endpoints require a valid JWT AND the scope {@code user.read}.
 * Try calling them without a token → 401. With a token missing the scope → 403.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ScopeAllowed({"user.read"})
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @ScopeAllowed({"user.read"})
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
