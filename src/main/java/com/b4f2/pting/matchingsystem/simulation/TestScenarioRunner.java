package com.b4f2.pting.matchingsystem.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.matchingsystem.algorithm.MatchingAlgorithm;
import com.b4f2.pting.matchingsystem.data.TestDataGenerator;
import com.b4f2.pting.matchingsystem.model.Player;
import com.b4f2.pting.matchingsystem.service.MatchingService;

@Component
@RequiredArgsConstructor
public class TestScenarioRunner {

    private final MatchingService matchingService;

    public void runAllScenarios(List<MatchingAlgorithm> algorithms, int count, int rounds) {
        double mean = 1500;
        double stddev = 200;

        List<Player> data = new TestDataGenerator().generatePlayers(count, mean, stddev);

        // 농구(10인 게임)
        System.out.println("=== 농구(10명) 알고리즘 평가 ===");
        List<AlgorithmResult> basketballResults = new ArrayList<>();
        for (MatchingAlgorithm algorithm : algorithms) {
            double avgScore = runFixedShuffledSimulation(algorithm, data, 10, rounds);
            basketballResults.add(new AlgorithmResult(algorithm.getName(), avgScore));
        }
        printRanking(basketballResults);

        // 풋살(20인 게임)
        System.out.println("\n=== 풋살(20명) 알고리즘 평가 ===");
        List<AlgorithmResult> futsalResults = new ArrayList<>();
        for (MatchingAlgorithm algorithm : algorithms) {
            double avgScore = runFixedShuffledSimulation(algorithm, data, 20, rounds);
            futsalResults.add(new AlgorithmResult(algorithm.getName(), avgScore));
        }
        printRanking(futsalResults);
    }

    private double runFixedShuffledSimulation(MatchingAlgorithm algorithm, List<Player> players, int playersPerGame,
        int rounds) {
        double totalScore = 0;

        for (int i = 0; i < rounds; i++) {
            List<Player> shuffledPlayers = new ArrayList<>(players);
            Collections.shuffle(shuffledPlayers);
            totalScore += matchingService.runAlgorithm(algorithm, shuffledPlayers, playersPerGame);
        }

        return totalScore / rounds;
    }

    private void printRanking(List<AlgorithmResult> results) {
        results.sort((a, b) -> Double.compare(b.avgScore, a.avgScore));

        System.out.println("알고리즘 점수 순위");

        for (int i = 0; i < results.size(); i++) {
            AlgorithmResult result = results.get(i);
            System.out.println((i + 1) + ". " + result.name + ": " + result.avgScore);
        }
    }

    private static class AlgorithmResult {

        String name;
        double avgScore;

        public AlgorithmResult(String name, double avgScore) {
            this.name = name;
            this.avgScore = avgScore;
        }
    }
}
