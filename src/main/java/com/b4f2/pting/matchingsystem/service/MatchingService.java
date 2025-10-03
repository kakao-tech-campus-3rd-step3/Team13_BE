package com.b4f2.pting.matchingsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.matchingsystem.algorithm.MatchingAlgorithm;
import com.b4f2.pting.matchingsystem.model.MatchResult;
import com.b4f2.pting.matchingsystem.model.Player;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final EvaluationService evaluationService;

    public double runAlgorithm(MatchingAlgorithm algorithm, List<Player> players, int playersPerGame) {
        int totalPlayers = players.size();

        List<MatchResult> results = algorithm.match(players, playersPerGame);
        return evaluationService.evaluate(results, players, totalPlayers);
    }
}
