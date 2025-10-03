package com.b4f2.pting.matchingsystem.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.b4f2.pting.matchingsystem.model.MatchResult;
import com.b4f2.pting.matchingsystem.model.Player;


@Service
public class EvaluationService {

    /**
     * 매칭률 반영 + 팀 내 균형 + 팀 간 균형 + 매칭 공정성
     */
    public double evaluate(List<MatchResult> matches, List<Player> players, int totalPlayers) {
        // 매칭률 계산
        Set<Integer> matchedIds = matches.stream()
            .flatMap(m -> m.getMatch().stream())
            .map(Player::getId)
            .collect(Collectors.toSet());
        double matchingRate = (double) matchedIds.size() / totalPlayers;

        // 팀 내 분산 평균 계산
        double avgIntraVar = matches.stream().mapToDouble(match -> {
            List<Player> team = match.getMatch();
            double avg = team.stream().mapToDouble(Player::getMmr).average().orElse(0);
            return team.stream().mapToDouble(p -> Math.pow(p.getMmr() - avg, 2)).average().orElse(0);
        }).average().orElse(0);

        // 팀 내 균형 점수 (분산이 크면 0, 작으면 1)
        double intraScore = 1 / (1 + avgIntraVar / 1000); // 1000으로 스케일 조정

        // 팀 간 평균 MMR 분산 계산
        List<Double> teamAverages = matches.stream()
            .map(m -> m.getMatch().stream().mapToDouble(Player::getMmr).average().orElse(0))
            .toList();
        double overallAvg = teamAverages.stream().mapToDouble(d -> d).average().orElse(0);
        double interVar = teamAverages.stream().mapToDouble(avg -> Math.pow(avg - overallAvg, 2)).sum();

        // 팀 간 균형 점수
        double interScore = 1 / (1 + interVar / 1000); // 1000으로 스케일 조정

        // 공정성 점수: 플레이어별 matchCount 분산 기반 (0~1)
        double avgMatches = players.stream().mapToDouble(Player::getMatchCount).average().orElse(0);
        double fairnessVar = players.stream()
            .mapToDouble(p -> Math.pow(p.getMatchCount() - avgMatches, 2))
            .average()
            .orElse(0);
        double fairnessScore = 1 / (1 + fairnessVar);

        // 최종 점수
        double matchingScore = 100 * matchingRate;
        double intraScoreScaled = 100 * intraScore;
        double interScoreScaled = 100 * interScore;
        double fairnessScoreScaled = 100 * fairnessScore;

        double sumScore = matchingScore + intraScoreScaled + interScoreScaled + fairnessScoreScaled;
        double finalScore = sumScore / 4;

        return finalScore;
    }
}
