package com.b4f2.pting.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.b4f2.pting.domain.TimePeriod;

@Component
public class StringToTimePeriodConverter implements Converter<String, TimePeriod> {

    @Override
    public TimePeriod convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        try {
            return TimePeriod.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 TimePeriod 입니다.");
        }
    }
}
