package com.example.sensorreadings.util;

import com.example.sensorreadings.model.Statistic;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StatisticCalculatorTest {
    
    private static final List<BigDecimal> VALUES = List.of(
            new BigDecimal("23"),
            new BigDecimal("32"),
            new BigDecimal("20"),
            new BigDecimal("22")
    );
    
    @Test
    void shouldReturnAverage() {
        BigDecimal result = StatisticCalculator.apply(VALUES, Statistic.AVERAGE);
        
        assertEquals(new BigDecimal("24.25"), result);
    }
    
    @Test
    void shouldReturnMin() {
        BigDecimal result = StatisticCalculator.apply(VALUES, Statistic.MIN);
        
        assertEquals(new BigDecimal("20"), result);
    }
    
    @Test
    void shouldReturnMax() {
        BigDecimal result = StatisticCalculator.apply(VALUES, Statistic.MAX);
        
        assertEquals(new BigDecimal("32"), result);
    }
    
    @Test
    void shouldReturnNullWhenValuesAreEmpty() {
        BigDecimal result = StatisticCalculator.apply(List.of(), Statistic.AVERAGE);
        
        assertNull(result);
    }
}