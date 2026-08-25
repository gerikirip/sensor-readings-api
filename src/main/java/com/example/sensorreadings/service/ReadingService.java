package com.example.sensorreadings.service;

import com.example.sensorreadings.model.ReadingQuery;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.reading.ReadingLoader;
import com.example.sensorreadings.util.TemperatureConverter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadingService {

    private final List<SensorReading> readings;

    public ReadingService(ReadingLoader readingLoader) {
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

        return matching.stream()
                .map(reading -> convertTemperature(reading, query))
                .toList();
    }

    private boolean matchesDevice(SensorReading reading, ReadingQuery query) {
        return query.deviceIds() == null
                || query.deviceIds().isEmpty()
                || query.deviceIds().contains(reading.deviceId());
    }

    private SensorReading convertTemperature(SensorReading reading, ReadingQuery query) {
        return new SensorReading(
                reading.deviceId(),
                reading.measureTime(),
                TemperatureConverter.convert(reading.temperature(), query.unit()),
                reading.humidity()
        );
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
}
