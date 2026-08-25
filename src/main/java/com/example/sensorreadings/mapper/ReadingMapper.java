package com.example.sensorreadings.mapper;

import com.example.sensorreadings.dto.DeviceReadingResponse;
import com.example.sensorreadings.dto.ReadingResponse;
import com.example.sensorreadings.model.Metric;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.Statistic;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ReadingMapper {

    public ReadingResponse toResponse(List<SensorReading> readings, Statistic statistic, Set<Metric> metrics) {
        List<DeviceReadingResponse> results = readings.stream()
                .map(reading -> toDeviceReadingResponse(reading, metrics))
                .toList();

        return new ReadingResponse(statistic, results);
    }

    public DeviceReadingResponse toDeviceReadingResponse(SensorReading reading, Set<Metric> metrics) {
        return new DeviceReadingResponse(
                reading.deviceId(),
                includes(metrics, Metric.TEMPERATURE) ? reading.temperature() : null,
                includes(metrics, Metric.HUMIDITY) ? reading.humidity() : null
        );
    }

    private boolean includes(Set<Metric> metrics, Metric metric) {
        return metrics == null || metrics.isEmpty() || metrics.contains(metric);
    }
}
