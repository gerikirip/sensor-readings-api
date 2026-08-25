package com.example.sensorreadings.reading.imp;

import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.TemperatureUnit;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CsvReadingLoaderTest {

    private static final String CSV = """
            DeviceId,MeasureTime,Temperature,TempUnit,Humidity
            1,2026-07-10T10:00:00Z,23,C,42
            2,2026-07-10T10:00:00Z,77,F,
            """;

    private final CsvReadingLoader loader = new CsvReadingLoader(
            new ByteArrayResource(CSV.getBytes(StandardCharsets.UTF_8))
    );

    @Test
    void shouldLoadAllReadings() {
        List<SensorReading> readings = loader.load();

        assertEquals(2, readings.size());
    }

    @Test
    void shouldParseCelsiusReading() {
        List<SensorReading> readings = loader.load();
        SensorReading reading = readings.getFirst();

        assertAll(
                () -> assertEquals(1L, reading.deviceId()),
                () -> assertEquals(Instant.parse("2026-07-10T10:00:00Z"), reading.measureTime()),
                () -> assertEquals(new BigDecimal("23"), reading.temperature().value()),
                () -> assertEquals(TemperatureUnit.C, reading.temperature().unit()),
                () -> assertEquals(new BigDecimal("42"), reading.humidity())
        );
    }

    @Test
    void shouldParseFahrenheitReadingAndMissingHumidity() {
        List<SensorReading> readings = loader.load();
        SensorReading reading = readings.get(1);

        assertAll(
                () -> assertEquals(2L, reading.deviceId()),
                () -> assertEquals(
                        Instant.parse("2026-07-10T10:00:00Z"),
                        reading.measureTime()
                ),
                () -> assertEquals(
                        new BigDecimal("77"),
                        reading.temperature().value()
                ),
                () -> assertEquals(
                        TemperatureUnit.F,
                        reading.temperature().unit()
                ),
                () -> assertNull(reading.humidity())
        );
    }
}
