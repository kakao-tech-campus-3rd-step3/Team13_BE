package com.b4f2.pting.domain;

import java.time.LocalTime;

import lombok.Getter;

@Getter
public enum TimePeriod {
    MORNING(
        LocalTime.of(6, 0),
        LocalTime.of(11, 59, 59)
    ),
    NOON(
        LocalTime.of(12, 0),
        LocalTime.of(17, 59, 59)
    ),
    EVENING(
        LocalTime.of(18, 0),
        LocalTime.of(23, 59, 59)
    );

    private final LocalTime startTime;
    private final LocalTime endTime;

    TimePeriod(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
