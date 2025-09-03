package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameResponse;
import com.b4f2.pting.service.GameService;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@Login Member member, @RequestBody CreateGameRequest request) {
        return ResponseEntity.ok(gameService.createGame(member, request));
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<Void> joinGame(@Login Member member, @PathVariable Long gameId) {
        gameService.joinGame(member, gameId);
        return ResponseEntity.ok().build();
    }
}
