package com.example.sensorreadings.reading.imp;

import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import com.example.sensorreadings.reading.ReadingLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Component
public class CsvReadingLoader implements ReadingLoader {

    private final Resource resource;

    public CsvReadingLoader(
            @Value("${app.readings.location}")
            Resource resource
    ) {
        this.resource = resource;
    }

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();

    @Override
    public List<SensorReading> load() {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSV_FORMAT.parse(reader)) {

            return parser.stream().map(this::toSensorReading).toList();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to load " + resource.getDescription(),
                    exception
            );
        }
    }

    private SensorReading toSensorReading(CSVRecord record) {
        try {
            return new SensorReading(
                    Long.parseLong(record.get("DeviceId")),
                    Instant.parse(record.get("MeasureTime")),
                    parseTemperature(record),
                    parseNullableDecimal(record.get("Humidity"))
            );
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "Invalid CSV record at row " + record.getRecordNumber(),
                    exception
            );
        }
    }

    private Temperature parseTemperature(CSVRecord record) {
        BigDecimal value = new BigDecimal(record.get("Temperature"));
        TemperatureUnit unit = TemperatureUnit.valueOf(
                record.get("TempUnit").toUpperCase(Locale.ROOT)
        );

        return new Temperature(value, unit);
    }

    private BigDecimal parseNullableDecimal(String value) {
        return value.isBlank() ? null : new BigDecimal(value);
    }
}