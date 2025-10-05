package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.TimePeriod;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameDetailResponse;
import com.b4f2.pting.dto.GamesResponse;
import com.b4f2.pting.dto.VoteRequest;
import com.b4f2.pting.dto.VoteResultResponse;
import com.b4f2.pting.service.GameService;
import com.b4f2.pting.service.RankGameService;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final RankGameService rankGameService;

    @PostMapping
    public ResponseEntity<GameDetailResponse> createGame(
        @Login Member member,
        @RequestBody CreateGameRequest request
    ) {
        return ResponseEntity.ok(gameService.createGame(member, request));
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<Void> joinGame(
        @Login Member member,
        @PathVariable Long gameId
    ) throws FirebaseMessagingException {
        gameService.joinGame(member, gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<GamesResponse> getGamesBySportIdAndTimePeriod(
        @RequestParam Long sportId,
        @RequestParam(required = false) TimePeriod timePeriod
    ) {
        return ResponseEntity.ok(gameService.findGamesBySportIdAndTimePeriod(sportId, timePeriod));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDetailResponse> getGameById(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.findGameById(gameId));
    }

    @PostMapping("/{gameId}/votes")
    public ResponseEntity<VoteResultResponse> voteMatchResult(
        @Login Member member,
        @RequestBody VoteRequest voteRequest,
        @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(rankGameService.voteMatchResult(gameId, voteRequest, member));
    }
}
