package com.example.sensorreadings.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "must not be blank")
        @Schema(description = "Username", example = "admin")
        String username,

        @NotBlank(message = "must not be blank")
        @Schema(description = "Password", example = "admin")
        String password
) {
}
