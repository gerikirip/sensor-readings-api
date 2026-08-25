package com.example.sensorreadings.model;

import java.math.BigDecimal;
import java.time.Instant;

public record SensorReading(
    long deviceId,
    Instant measureTime,
    Temperature temperature,
    BigDecimal humidity
) {
}