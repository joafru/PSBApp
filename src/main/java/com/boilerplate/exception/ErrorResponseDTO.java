package com.boilerplate.exception;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorResponseDTO {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    /** Populated only on validation errors (field → message). */
    private Map<String, String> fieldErrors;
}
