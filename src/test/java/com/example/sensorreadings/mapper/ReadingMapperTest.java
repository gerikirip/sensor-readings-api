package com.example.sensorreadings.mapper;

import com.example.sensorreadings.dto.DeviceReadingResponse;
import com.example.sensorreadings.dto.ReadingResponse;
import com.example.sensorreadings.model.Metric;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.Statistic;
import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReadingMapperTest {

    private static final Temperature TEMPERATURE = new Temperature(
            new BigDecimal("22"),
            TemperatureUnit.C
    );
    private static final BigDecimal HUMIDITY = new BigDecimal("44");

    private final ReadingMapper mapper = new ReadingMapper();
    private final SensorReading reading = new SensorReading(
            1L,
            Instant.parse("2026-07-11T10:00:00Z"),
            TEMPERATURE,
            HUMIDITY
    );

    @Test
    void shouldIncludeBothMetricsWhenMetricsAreOmitted() {
        ReadingResponse response = mapper.toResponse(List.of(reading), Statistic.AVERAGE, null);
        DeviceReadingResponse result = response.results().getFirst();

        assertAll(
                () -> assertEquals(TEMPERATURE, result.temperature()),
                () -> assertEquals(HUMIDITY, result.humidity())
        );
    }

    @Test
    void shouldOmitHumidityWhenOnlyTemperatureIsRequested() {
        ReadingResponse response = mapper.toResponse(
                List.of(reading),
                Statistic.AVERAGE,
                Set.of(Metric.TEMPERATURE)
        );
        DeviceReadingResponse result = response.results().getFirst();

        assertAll(
                () -> assertEquals(TEMPERATURE, result.temperature()),
                () -> assertNull(result.humidity())
        );
    }

    @Test
    void shouldOmitTemperatureWhenOnlyHumidityIsRequested() {
        ReadingResponse response = mapper.toResponse(
                List.of(reading),
                Statistic.AVERAGE,
                Set.of(Metric.HUMIDITY)
        );
        DeviceReadingResponse result = response.results().getFirst();

        assertAll(
                () -> assertNull(result.temperature()),
                () -> assertEquals(HUMIDITY, result.humidity())
        );
    }
}
