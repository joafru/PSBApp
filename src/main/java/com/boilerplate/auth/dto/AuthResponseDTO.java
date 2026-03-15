package com.boilerplate.auth.dto;

import com.boilerplate.user.dto.UserResponseDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthResponseDTO {

    private String accessToken;
    /** Token TTL in milliseconds (same value as app.jwt.expiration). */
    private long expiration;
    private UserResponseDTO user;
}
