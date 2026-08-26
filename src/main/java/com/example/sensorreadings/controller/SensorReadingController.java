package com.example.sensorreadings.controller;

import com.example.sensorreadings.dto.ReadingResponse;
import com.example.sensorreadings.mapper.ReadingMapper;
import com.example.sensorreadings.model.Metric;
import com.example.sensorreadings.model.ReadingQuery;
import com.example.sensorreadings.model.Statistic;
import com.example.sensorreadings.model.TemperatureUnit;
import com.example.sensorreadings.service.SensorReadingService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;
    private final ReadingMapper readingMapper;

    @GetMapping
    public ReadingResponse query(
            @Parameter(description = "Device ids. Omit to include all devices")
            @RequestParam(required = false) Set<Long> devices,

            @Parameter(description = "Inclusive start date. If both from and to are omitted, the latest reading per device is used. Example: 2026-07-10T00:00:00Z")
            @RequestParam(required = false) Instant from,

            @Parameter(description = "Inclusive end date. If both from and to are omitted, the latest reading per device is used. Example: 2026-07-11T23:59:59Z")
            @RequestParam(required = false) Instant to,

            @Parameter(description = "TEMPERATURE, HUMIDITY, or both. Omit to include both")
            @RequestParam(required = false) Set<Metric> metrics,

            @Parameter(description = "MIN, MAX, or AVERAGE. Defaults to AVERAGE if omitted")
            @RequestParam(required = false) Statistic statistic,

            @Parameter(description = "Response temperature scale. All readings are converted to this unit because the CSV mixes Celsius and Fahrenheit", required = true)
            @RequestParam TemperatureUnit unit
    ) {
        ReadingQuery query = new ReadingQuery(
                devices,
                from,
                to,
                metrics,
                statistic == null ? Statistic.AVERAGE : statistic,
                unit
        );
        return readingMapper.toResponse(
                sensorReadingService.query(query),
                query.statistic(),
                query.metrics()
        );
    }
}
