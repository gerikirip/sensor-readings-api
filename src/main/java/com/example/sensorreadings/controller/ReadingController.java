package com.example.sensorreadings.controller;

import com.example.sensorreadings.dto.ReadingResponse;
import com.example.sensorreadings.mapper.ReadingMapper;
import com.example.sensorreadings.model.Metric;
import com.example.sensorreadings.model.ReadingQuery;
import com.example.sensorreadings.model.Statistic;
import com.example.sensorreadings.model.TemperatureUnit;
import com.example.sensorreadings.service.ReadingService;
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
public class ReadingController {

    private final ReadingService readingService;
    private final ReadingMapper readingMapper;

    @GetMapping
    public ReadingResponse query(
            @RequestParam(required = false) Set<Long> devices,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) Set<Metric> metrics,
            @RequestParam(required = false) Statistic statistic,
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
                readingService.query(query),
                query.statistic()
        );
    }
}