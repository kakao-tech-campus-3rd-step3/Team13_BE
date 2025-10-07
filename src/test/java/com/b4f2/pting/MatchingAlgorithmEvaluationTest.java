package com.b4f2.pting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.algorithm.MatchingAlgorithm;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Mmr;
import com.b4f2.pting.domain.Sport;

@SpringBootTest
public class MatchingAlgorithmEvaluationTest {

    @Autowired
    private List<MatchingAlgorithm> algorithms;

    static Stream<Sport> sportsProvider() {
        Sport basketball = new Sport("농구", 10);
        ReflectionTestUtils.setField(basketball, "id", 1L);

        Sport futsal = new Sport("풋살", 20);
        ReflectionTestUtils.setField(futsal, "id", 2L);

        return Stream.of(basketball, futsal);
    }

    @ParameterizedTest
    @MethodSource("sportsProvider")
    void testAllAlgorithms(Sport sport) {
        int totalPlayers = 50;
        int mean = 1500;
        int stddev = 200;

        // 데이터
        List<Member> players = generatePlayers(totalPlayers, mean, stddev, sport);

        Map<Member, Integer> matchCounts = new HashMap<>();
        players.forEach(m -> matchCounts.put(m, 0));

        // 매칭
        Map<String, Double> scores = new HashMap<>();
        for (MatchingAlgorithm algorithm : algorithms) {
            List<List<Member>> matches = algorithm.match(players, sport);

            for (List<Member> match : matches) {
                for (Member player : match) {
                    matchCounts.put(player, matchCounts.get(player) + 1);
                }
            }

            double score = evaluate(sport, matches, players, totalPlayers, matchCounts);
            scores.put(algorithm.getName(), score);
        }

        // 평가
        printRanking(sport.getName(), scores);
    }

    private List<Member> generatePlayers(int count, double mean, double stddev, Sport sport) {
        long seed = 42L;
        Random random = new Random(seed);

        List<Member> players = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double gaussian = random.nextGaussian();
            double mmrValue = mean + gaussian * stddev;

            Member member = new Member(1000L + i, Member.OAuthProvider.KAKAO);
            ReflectionTestUtils.setField(member, "id", (long) i);
            ReflectionTestUtils.setField(member, "name", "Player" + i);

            Mmr mmr = new Mmr(sport, member);
            ReflectionTestUtils.setField(mmr, "mu", mmrValue);

            ReflectionTestUtils.setField(member, "mmrList", List.of(mmr));

            players.add(member);
        }

        return players;
    }

    private double evaluate(Sport sport, List<List<Member>> matches, List<Member> players, int totalPlayers,
            Map<Member, Integer> matchCounts) {
        // 매칭률 계산
        Set<Long> matchedIds = matches.stream()
                .flatMap(List::stream)
                .map(Member::getId)
                .collect(Collectors.toSet());
        double matchingRate = (double) matchedIds.size() / totalPlayers;

        // 팀 내 분산 평균 계산
        double avgIntraVar = matches.stream().mapToDouble(match -> {
            double avg = match.stream().mapToDouble(m -> m.getMmr(sport)).average().orElse(0);
            return match.stream().mapToDouble(m -> Math.pow(m.getMmr(sport) - avg, 2)).average().orElse(0);
        }).average().orElse(0);

        double intraScore = 1 / (1 + avgIntraVar / 1000);

        // 팀 간 평균 MMR 분산 계산
        List<Double> teamAverages = matches.stream()
                .map(m -> m.stream().mapToDouble(p -> p.getMmr(sport)).average().orElse(0))
                .toList();
        double overallAvg = teamAverages.stream().mapToDouble(d -> d).average().orElse(0);
        double interVar = teamAverages.stream().mapToDouble(avg -> Math.pow(avg - overallAvg, 2)).average().orElse(0);
        double interScore = 1 / (1 + interVar / 1000);

        // 공정성 점수
        double avgMatches = players.stream().mapToInt(m -> matchCounts.getOrDefault(m, 0)).average().orElse(0);
        double fairnessVar = players.stream().mapToDouble(m -> Math.pow(matchCounts.getOrDefault(m, 0) - avgMatches, 2))
                .average().orElse(0);
        double fairnessScore = 1 / (1 + fairnessVar);

        // 최종 점수
        double sumScore = 100 * matchingRate + 100 * intraScore + 100 * interScore + 100 * fairnessScore;
        return sumScore / 4;
    }

    private void printRanking(String title, Map<String, Double> scores) {
        System.out.println("\n=== " + title + " ===");
        scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
