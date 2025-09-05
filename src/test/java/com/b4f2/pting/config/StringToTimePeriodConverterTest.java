package com.b4f2.pting.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b4f2.pting.domain.TimePeriod;

public class StringToTimePeriodConverterTest {

    private StringToTimePeriodConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToTimePeriodConverter();
    }

    @ParameterizedTest
    @ValueSource(strings = {"noon", "MORNING", "EVENING"})
    void 옳바른_값_변환(String input) {
        TimePeriod period = converter.convert(input);

        assert(period instanceof TimePeriod);
    }

    @Test
    void 옮바르지_않은_값_변환() {
        assertThrows(
            IllegalArgumentException.class,
            () -> converter.convert("asdf")
        );
    }


}
