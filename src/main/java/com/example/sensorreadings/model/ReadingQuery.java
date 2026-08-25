package com.example.sensorreadings.model;

import java.time.Instant;
import java.util.Set;

public record ReadingQuery(
    Set<Long> deviceIds,
    Instant from,
    Instant to,
    Set<Metric> metrics,
    Statistic statistic,
    TemperatureUnit unit
) {
}