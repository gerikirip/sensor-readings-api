package com.example.sensorreadings.service;

import com.example.sensorreadings.model.ReadingQuery;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import com.example.sensorreadings.reading.ReadingLoader;
import com.example.sensorreadings.util.StatisticCalculator;
import com.example.sensorreadings.util.TemperatureConverter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorReadingService {

    private final List<SensorReading> readings;

    public SensorReadingService(ReadingLoader readingLoader) {
        this.readings = readingLoader.load();
    }

    public List<SensorReading> query(ReadingQuery query) {
        List<SensorReading> matching = readings.stream()
                .filter(reading -> matchesDevice(reading, query))
                .filter(reading -> matchesDateRange(reading, query))
                .toList();

        if (query.from() == null && query.to() == null) {
            matching = latestPerDevice(matching);
        }

        return aggregatePerDevice(matching, query);
    }

    private boolean matchesDevice(SensorReading reading, ReadingQuery query) {
        return query.deviceIds() == null
                || query.deviceIds().isEmpty()
                || query.deviceIds().contains(reading.deviceId());
    }

    private boolean matchesDateRange(SensorReading reading, ReadingQuery query) {
        Instant measureTime = reading.measureTime();
        if (query.from() != null && measureTime.isBefore(query.from())) {
            return false;
        }
        if (query.to() != null && measureTime.isAfter(query.to())) {
            return false;
        }
        return true;
    }

    private List<SensorReading> latestPerDevice(List<SensorReading> readings) {
        Map<Long, SensorReading> latestByDevice = new LinkedHashMap<>();
        for (SensorReading reading : readings) {
            SensorReading current = latestByDevice.get(reading.deviceId());
            if (current == null || reading.measureTime().isAfter(current.measureTime())) {
                latestByDevice.put(reading.deviceId(), reading);
            }
        }
        return List.copyOf(latestByDevice.values());
    }

    private List<SensorReading> aggregatePerDevice(List<SensorReading> readings, ReadingQuery query) {
        Map<Long, List<SensorReading>> byDevice = new LinkedHashMap<>();
        for (SensorReading reading : readings) {
            byDevice.computeIfAbsent(reading.deviceId(), id -> new ArrayList<>()).add(reading);
        }

        List<SensorReading> aggregated = new ArrayList<>();
        for (Map.Entry<Long, List<SensorReading>> entry : byDevice.entrySet()) {
            aggregated.add(aggregateDevice(entry.getKey(), entry.getValue(), query));
        }

        return aggregated;
    }

    private SensorReading aggregateDevice(long deviceId, List<SensorReading> readings, ReadingQuery query) {
        List<BigDecimal> temperatures = new ArrayList<>();
        List<BigDecimal> humidities = new ArrayList<>();
        for (SensorReading reading : readings) {
            temperatures.add(TemperatureConverter.toCelsius(reading.temperature()));
            if (reading.humidity() != null) {
                humidities.add(reading.humidity());
            }
        }

        BigDecimal temperatureC = StatisticCalculator.apply(temperatures, query.statistic());
        BigDecimal humidity = StatisticCalculator.apply(humidities, query.statistic());

        Temperature temperature = TemperatureConverter.convert(
                new Temperature(temperatureC, TemperatureUnit.C),
                query.unit()
        );

        return new SensorReading(
                deviceId,
                readings.getFirst().measureTime(),
                temperature,
                humidity
        );
    }
}
