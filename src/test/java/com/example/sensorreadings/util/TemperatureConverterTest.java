package com.example.sensorreadings.util;

import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemperatureConverterTest {

    @Test
    void shouldKeepCelsiusUnchanged() {
        Temperature temperature = new Temperature(new BigDecimal("23"), TemperatureUnit.C);

        Temperature converted = TemperatureConverter.convert(temperature, TemperatureUnit.C);

        assertEquals(temperature, converted);
    }
    
    @Test
    void shouldKeepFahrenheitUnchanged() {
        Temperature temperature = new Temperature(new BigDecimal("77"), TemperatureUnit.F);

        Temperature converted = TemperatureConverter.convert(temperature, TemperatureUnit.F);

        assertEquals(temperature, converted);
    }

    @Test
    void shouldConvertFahrenheitToCelsius() {
        Temperature temperature = new Temperature(new BigDecimal("77"), TemperatureUnit.F);

        Temperature converted = TemperatureConverter.convert(temperature, TemperatureUnit.C);

        assertEquals(new BigDecimal("25.00"), converted.value());
        assertEquals(TemperatureUnit.C, converted.unit());
    }

    @Test
    void shouldConvertCelsiusToFahrenheit() {
        Temperature temperature = new Temperature(new BigDecimal("25"), TemperatureUnit.C);

        Temperature converted = TemperatureConverter.convert(temperature, TemperatureUnit.F);

        assertEquals(new BigDecimal("77.00"), converted.value());
        assertEquals(TemperatureUnit.F, converted.unit());
    }
}
