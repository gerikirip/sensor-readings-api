package com.example.sensorreadings.controller;

import com.example.sensorreadings.dto.DeviceReadingResponse;
import com.example.sensorreadings.dto.ReadingResponse;
import com.example.sensorreadings.exception.GlobalExceptionHandler;
import com.example.sensorreadings.mapper.ReadingMapper;
import com.example.sensorreadings.model.Statistic;
import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import com.example.sensorreadings.service.SensorReadingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SensorReadingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class SensorReadingControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    SensorReadingService sensorReadingService;

    @MockitoBean
    ReadingMapper readingMapper;

    @Test
    void shouldReturnReadingsWhenQuerySucceeds() throws Exception {
        when(sensorReadingService.query(any())).thenReturn(List.of());
        when(readingMapper.toResponse(any(), any(), any())).thenReturn(
                new ReadingResponse(
                        Statistic.AVERAGE,
                        List.of(new DeviceReadingResponse(
                                1L,
                                new Temperature(new BigDecimal("22.00"), TemperatureUnit.C),
                                new BigDecimal("44")
                        ))
                )
        );
        
        mockMvc.perform(get("/api/readings")
                    .param("unit", "C"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statistic").value("AVERAGE"))
            .andExpect(jsonPath("$.results[0].deviceId").value(1))
            .andExpect(jsonPath("$.results[0].temperature.value").value(22.00))
            .andExpect(jsonPath("$.results[0].temperature.unit").value("C"))
            .andExpect(jsonPath("$.results[0].humidity").value(44));
    }

    @Test
    void shouldReturnBadRequestWhenFromIsAfterTo() throws Exception {
        when(sensorReadingService.query(any())).thenThrow(new IllegalArgumentException("Invalid date range: from is later than to"));

        mockMvc.perform(get("/api/readings")
                        .param("from", "2026-07-11T10:00:00Z")
                        .param("to", "2026-07-10T10:00:00Z")
                        .param("unit", "C"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid date range: from is later than to"))
                .andExpect(jsonPath("$.path").value("/api/readings"));
    }
}