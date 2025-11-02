package com.b4f2.pting.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.b4f2.pting.domain.TimePeriod;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StringToTimePeriodConverterTest {

    private StringToTimePeriodConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToTimePeriodConverter();
    }

    @ParameterizedTest
    @ValueSource(strings = {"noon", "MORNING", "EVENING"})
    void convert_올바른값변환_성공(String input) {
        TimePeriod period = converter.convert(input);

        assert (period instanceof TimePeriod);
    }

    @Test
    void convert_올바르지않은값변환_실패() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert("asdf"));
    }
}
