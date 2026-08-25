package com.example.sensorreadings.model;

import java.math.BigDecimal;

public record Temperature(
    BigDecimal value,
    TemperatureUnit unit
) {
}