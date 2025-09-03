package com.b4f2.pting.dto;

import com.b4f2.pting.domain.Sport;

public record SportResponse(Long sportId, String name, Integer recommendedPlayerCount) {
    public SportResponse(Sport sport) {
        this(sport.getId(), sport.getName(), sport.getRecommendedPlayerCount());
    }
}
