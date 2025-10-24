package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Mmr;
import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.Sport;

class MatchingAlgorithmEvaluationTest {

    private List<MatchingAlgorithm> algorithms;

    @BeforeEach
    void setUp() {
        algorithms = List.of(
                new SimpleMMRMatching(),
                new BalancedKMeansClusterMatching(),
                new BalancedDPClusterMatching(),
                new MMRBucketMatching());
    }

    static Stream<Sport> sportsProvider() {
        Sport basketball = new Sport("농구", 10);
        ReflectionTestUtils.setField(basketball, "id", 1L);

        Sport futsal = new Sport("풋살", 10);
        ReflectionTestUtils.setField(futsal, "id", 2L);

        return Stream.of(basketball, futsal);
    }

    @ParameterizedTest
    @MethodSource("sportsProvider")
    void testAllAlgorithms(Sport sport) {
        int totalPlayers = 100;
        double mean = 25.0;
        double stddev = 4.0;
        int rounds = 10;
        int maxNewPerRound = 15;

        Map<String, List<Double>> algorithmScores = new HashMap<>();
        long seed = 42L;
        Random random = new Random(seed); // 한 번만 생성

        for (MatchingAlgorithm algorithm : algorithms) {
            // 전체 참가자 생성
            List<RankGameParticipant> allParticipants = generatePlayers(totalPlayers, mean, stddev, sport, seed);
            List<RankGameParticipant> remainingParticipants = new ArrayList<>();

            // 대기 라운드 기록
            Map<RankGameParticipant, Integer> waitRoundsMap = new HashMap<>();
            for (RankGameParticipant p : allParticipants) {
                waitRoundsMap.put(p, 0);
            }

            for (int round = 0; round < rounds; round++) {
                // 라운드마다 랜덤 수의 신규 참가자 추가
                int newCount = 1 + random.nextInt(maxNewPerRound);

                // remainingParticipants에 없는 참가자만 선택
                List<RankGameParticipant> availableParticipants = new ArrayList<>();
                for (RankGameParticipant p : allParticipants) {
                    if (!remainingParticipants.contains(p)) {
                        availableParticipants.add(p);
                    }
                }

                List<RankGameParticipant> newParticipants = new ArrayList<>();
                for (int i = 0; i < Math.min(newCount, availableParticipants.size()); i++) {
                    newParticipants.add(availableParticipants.get(i));
                }

                remainingParticipants.addAll(newParticipants);

                // 매칭
                List<List<RankGameParticipant>> matches = algorithm.match(remainingParticipants, sport);

                // 매칭된 사람 제거, 남은 사람은 다음 라운드로
                Set<RankGameParticipant> matchedThisRound = new HashSet<>();
                for (List<RankGameParticipant> match : matches) {
                    matchedThisRound.addAll(match);
                }

                List<RankGameParticipant> newRemaining = new ArrayList<>();
                for (RankGameParticipant p : remainingParticipants) {
                    if (!matchedThisRound.contains(p)) {
                        newRemaining.add(p);
                    }
                }
                remainingParticipants = newRemaining;

                // 대기 라운드 누적
                for (RankGameParticipant p : remainingParticipants) {
                    waitRoundsMap.put(p, waitRoundsMap.get(p) + 1);
                }

                // 라운드 점수 계산
                double score = evaluateWithWaitTime(sport, matches, waitRoundsMap);
                algorithmScores
                        .computeIfAbsent(algorithm.getName(), k -> new ArrayList<>())
                        .add(score);
            }
        }

        // 평균 점수 계산
        Map<String, Double> avgScores = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : algorithmScores.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(d -> d).average().orElse(0);
            avgScores.put(entry.getKey(), avg);
        }

        printRanking(sport.getName(), avgScores);
    }

    private List<RankGameParticipant> generatePlayers(int count, double mean, double stddev, Sport sport, Long seed) {
        Random random = new Random(seed);

        List<RankGameParticipant> participants = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double gaussian = random.nextGaussian();
            double mmrValue = mean + gaussian * stddev;

            Member member = new Member(1000L + i, Member.OAuthProvider.KAKAO);
            ReflectionTestUtils.setField(member, "id", (long) i);
            ReflectionTestUtils.setField(member, "name", "Player" + i);

            Mmr mmr = new Mmr(sport, member);
            ReflectionTestUtils.setField(mmr, "mu", mmrValue);
            ReflectionTestUtils.setField(member, "mmrList", List.of(mmr));

            RankGameParticipant participant = new RankGameParticipant();
            ReflectionTestUtils.setField(participant, "member", member);

            participants.add(participant);
        }

        return participants;
    }

    private double evaluateWithWaitTime(
            Sport sport, List<List<RankGameParticipant>> matches, Map<RankGameParticipant, Integer> waitRoundsMap) {
        // 팀 내 MMR 분산 계산
        double avgIntraVar = matches.stream()
                .mapToDouble(match -> {
                    double avg = match.stream()
                            .mapToDouble(p -> p.getMember().getMmr(sport))
                            .average()
                            .orElse(0);
                    return match.stream()
                            .mapToDouble(p -> Math.pow(p.getMember().getMmr(sport) - avg, 2))
                            .average()
                            .orElse(0);
                })
                .average()
                .orElse(0);
        double intraScore = 1 / (1 + avgIntraVar);

        // 대기 라운드 점수
        double avgWait =
                waitRoundsMap.values().stream().mapToInt(v -> v).average().orElse(0);
        double waitScore = 1 / (1 + avgWait / 10);

        // 최종 점수
        double sumScore = 100 * intraScore + 100 * waitScore;
        return sumScore / 2;
    }

    private void printRanking(String title, Map<String, Double> scores) {
        System.out.println("\n=== " + title + " ===");
        scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
