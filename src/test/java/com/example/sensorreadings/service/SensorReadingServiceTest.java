package com.example.sensorreadings.service;

import com.example.sensorreadings.model.ReadingQuery;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.Statistic;
import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SensorReadingServiceTest {

    private static final Instant T10 = Instant.parse("2026-07-10T10:00:00Z");
    private static final Instant T12 = Instant.parse("2026-07-10T12:00:00Z");
    private static final Instant T18 = Instant.parse("2026-07-10T18:00:00Z");

    private final SensorReadingService service = new SensorReadingService(() -> List.of(
            new SensorReading(1L, T10, new Temperature(new BigDecimal("23"), TemperatureUnit.C), null),
            new SensorReading(1L, T18, new Temperature(new BigDecimal("20"), TemperatureUnit.C), null),
            new SensorReading(2L, T10, new Temperature(new BigDecimal("25"), TemperatureUnit.C), null),
            new SensorReading(2L, T12, new Temperature(new BigDecimal("30"), TemperatureUnit.C), null)
    ));

    @Test
    void shouldReturnLatestReadingPerDeviceWhenDateRangeIsOmitted() {
        ReadingQuery query = new ReadingQuery(
                null,
                null,
                null,
                null,
                Statistic.AVERAGE,
                TemperatureUnit.C
        );

        List<SensorReading> result = service.query(query);

        assertEquals(2, result.size());
        assertAll(
                () -> assertEquals(1L, result.getFirst().deviceId()),
                () -> assertEquals(T18, result.getFirst().measureTime()),
                () -> assertEquals(new BigDecimal("20.00"), result.getFirst().temperature().value()),
                () -> assertEquals(2L, result.get(1).deviceId()),
                () -> assertEquals(T12, result.get(1).measureTime()),
                () -> assertEquals(new BigDecimal("30.00"), result.get(1).temperature().value())
        );
    }

    @Test
    void shouldReturnReadingsInsideInclusiveDateRange() {
        ReadingQuery query = new ReadingQuery(
                null,
                T10,
                T12,
                null,
                Statistic.AVERAGE,
                TemperatureUnit.C
        );

        List<SensorReading> result = service.query(query);

        assertEquals(2, result.size());
        assertAll(
                () -> assertEquals(1L, result.getFirst().deviceId()),
                () -> assertEquals(T10, result.getFirst().measureTime()),
                () -> assertEquals(2L, result.get(1).deviceId()),
                () -> assertEquals(T10, result.get(1).measureTime())
        );
    }

    @Test
    void shouldRejectDateRangeWhenFromIsAfterTo() {
        ReadingQuery query = new ReadingQuery(
                null,
                T18,
                T10,
                null,
                Statistic.AVERAGE,
                TemperatureUnit.C
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.query(query)
        );
        
        assertEquals("Invalid date range: from is later than to", exception.getMessage());
    }
}
