package com.b4f2.pting.dto;

import java.time.LocalDateTime;

public record CreateGameRequest(
    Long sportId,
    Integer playerCount,
    LocalDateTime startTime,
    Integer duration
) {
}
