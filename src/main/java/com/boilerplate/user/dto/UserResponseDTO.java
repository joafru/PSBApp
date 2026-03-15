package com.boilerplate.user.dto;

import com.boilerplate.user.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Set<String> scopes;

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .scopes(user.getScopes().stream()
                        .map(s -> s.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
