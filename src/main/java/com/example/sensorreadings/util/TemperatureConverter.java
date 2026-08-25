package com.example.sensorreadings.util;

import com.example.sensorreadings.model.Temperature;
import com.example.sensorreadings.model.TemperatureUnit;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public final class TemperatureConverter {

    private static final int SCALE = 2;

    public static BigDecimal toCelsius(Temperature temperature) {
        return switch (temperature.unit()) {
            case C -> temperature.value();
            case F -> temperature.value()
                    .subtract(BigDecimal.valueOf(32))
                    .multiply(BigDecimal.valueOf(5))
                    .divide(BigDecimal.valueOf(9), SCALE, RoundingMode.HALF_UP);
        };
    }

    public static Temperature convert(Temperature temperature, TemperatureUnit target) {
        if (temperature.unit() == target) {
            return temperature;
        }
        if (target == TemperatureUnit.C) {
            return new Temperature(toCelsius(temperature), TemperatureUnit.C);
        }

        BigDecimal fahrenheit = toCelsius(temperature)
                .multiply(BigDecimal.valueOf(9))
                .divide(BigDecimal.valueOf(5), SCALE, RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(32));
        return new Temperature(fahrenheit, TemperatureUnit.F);
    }
}
