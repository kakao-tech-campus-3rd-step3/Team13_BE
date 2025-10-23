package com.b4f2.pting.dto;

import java.time.LocalDateTime;

import com.b4f2.pting.domain.Game;

public record GameResponse(
        Long gameId,
        Long sportId,
        String name,
        Integer playerCount,
        Game.GameStatus gameStatus,
        LocalDateTime startTime,
        Integer duration) {

    public GameResponse(Game game) {
        this(
                game.getId(),
                game.getSport().getId(),
                game.getName(),
                game.getPlayerCount(),
                game.getGameStatus(),
                game.getStartTime(),
                game.getDuration());
    }
}
