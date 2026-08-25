package com.example.sensorreadings.dto;

import com.example.sensorreadings.model.Temperature;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeviceReadingResponse(
        @Schema(description = "Device identifier", example = "1")
        long deviceId,
        @Schema(description = "Aggregated temperature, or null if not requested")
        Temperature temperature,
        @Schema(description = "Aggregated humidity, or null if missing or not requested", example = "42")
        BigDecimal humidity
) {
}