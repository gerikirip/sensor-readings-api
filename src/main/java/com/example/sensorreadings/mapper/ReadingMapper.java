package com.example.sensorreadings.mapper;

import com.example.sensorreadings.dto.DeviceReadingResponse;
import com.example.sensorreadings.dto.ReadingResponse;
import com.example.sensorreadings.model.SensorReading;
import com.example.sensorreadings.model.Statistic;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadingMapper {

    public ReadingResponse toResponse(List<SensorReading> readings, Statistic statistic) {
        List<DeviceReadingResponse> results = readings.stream()
                .map(this::toDeviceReadingResponse)
                .toList();

        return new ReadingResponse(statistic, results);
    }

    public DeviceReadingResponse toDeviceReadingResponse(SensorReading reading) {
        return new DeviceReadingResponse(
                reading.deviceId(),
                reading.temperature(),
                reading.humidity()
        );
    }
}
