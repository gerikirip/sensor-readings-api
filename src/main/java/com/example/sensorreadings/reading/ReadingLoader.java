package com.example.sensorreadings.reading;

import com.example.sensorreadings.model.SensorReading;

import java.util.List;

public interface ReadingLoader {
    List<SensorReading> load();
}