package com.example.sensorreadings.util;

import com.example.sensorreadings.model.Statistic;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@UtilityClass
public class StatisticCalculator {

    private static final int SCALE = 2;

    public static BigDecimal apply(List<BigDecimal> values, Statistic statistic) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        return switch (statistic) {
            case MIN -> values.stream().min(BigDecimal::compareTo).orElseThrow();
            case MAX -> values.stream().max(BigDecimal::compareTo).orElseThrow();
            case AVERAGE -> values.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(values.size()), SCALE, RoundingMode.HALF_UP);
        };
    }
}