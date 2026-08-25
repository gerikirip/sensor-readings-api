package com.example.sensorreadings.dto;

import com.example.sensorreadings.model.Statistic;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReadingResponse(
        @Schema(description = "Statistic applied to the metrics", example = "AVERAGE")
        Statistic statistic,
        List<DeviceReadingResponse> results
) {
}