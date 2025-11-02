package com.b4f2.pting.dto;

import java.time.LocalDateTime;

import com.b4f2.pting.domain.Game;

public record GameDetailResponse(
        Long gameId,
        Long sportId,
        String name,
        String gameLocation,
        Integer playerCount,
        Integer currentPlayerCount,
        Game.GameStatus gameStatus,
        LocalDateTime startTime,
        Integer duration,
        String description) {

    public GameDetailResponse(Game game, int currentPlayerCount) {
        this(
                game.getId(),
                game.getSport().getId(),
                game.getName(),
                game.getGameLocation(),
                game.getPlayerCount(),
                currentPlayerCount,
                game.getGameStatus(),
                game.getStartTime(),
                game.getDuration(),
                game.getDescription());
    }
}
