package com.example.sensorreadings.service;

import com.example.sensorreadings.model.ReadingQuery;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.reading.ReadingLoader;
import com.example.sensorreadings.util.TemperatureConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingService {

    private final List<SensorReading> readings;

    public ReadingService(ReadingLoader readingLoader) {
        this.readings = readingLoader.load();
    }

    public List<SensorReading> query(ReadingQuery query) {
        return readings.stream()
                .filter(reading -> matchesDevice(reading, query))
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
}
