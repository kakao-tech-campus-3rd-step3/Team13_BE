package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.Sport;

public class BalancedKMeansClusterMatching implements MatchingAlgorithm {

    private static final int MAX_ITER = 100;
    private static final double CONVERGED_THRESHOLD = 1e-4;

    @Override
    public String getName() {
        return "BalancedKMeansCluster";
    }

    @Override
    public List<List<RankGameParticipant>> match(List<RankGameParticipant> participants, Sport sport) {
        int teamSize = sport.getRecommendedPlayerCount();

        if (participants == null || participants.isEmpty() || teamSize <= 0) {
            return List.of();
        }

        int totalPlayers = participants.size();
        int numTeams = (int) Math.ceil((double) totalPlayers / teamSize);

        List<List<RankGameParticipant>> teams = new ArrayList<>();

        for (int teamIndex = 0; teamIndex < numTeams; teamIndex++) {
            teams.add(new ArrayList<>());
        }

        List<Double> mmrs = participants.stream()
            .map(participant -> participant.getMember().getMmr(sport))
            .toList();

        List<Double> centroids = initialCentroids(mmrs, numTeams);

        for (int iter = 0; iter < MAX_ITER; iter++) {
            allocateMembersToTeams(participants, sport, numTeams, centroids, teams, teamSize);

            List<Double> newCentroids = renewalCentroids(sport, teams, centroids);

            if (isConverged(numTeams, newCentroids, centroids)) {
                break;
            }

            centroids = newCentroids;
        }

        return teams;
    }

    private List<Double> initialCentroids(List<Double> mmrs, int numTeams) {
        double min = Collections.min(mmrs);
        double max = Collections.max(mmrs);
        List<Double> centroids = new ArrayList<>();
        for (int teamIndex = 0; teamIndex < numTeams; teamIndex++) {
            centroids.add(min + (max - min) * teamIndex / numTeams);
        }
        return centroids;
    }

    private void allocateMembersToTeams(
        List<RankGameParticipant> players,
        Sport sport,
        int numTeams,
        List<Double> centroids,
        List<List<RankGameParticipant>> teams,
        int teamSize) {
        teams.forEach(List::clear);

        for (RankGameParticipant p : players) {
            double mmr = p.getMember().getMmr(sport);

            List<Integer> sortedTeams = new ArrayList<>();
            for (int teamIndex = 0; teamIndex < numTeams; teamIndex++) {
                sortedTeams.add(teamIndex);
            }

            sortedTeams.sort(Comparator.comparingDouble(i -> Math.abs(mmr - centroids.get(i))));

            for (int idx : sortedTeams) {
                if (teams.get(idx).size() < teamSize) {
                    teams.get(idx).add(p);
                    break;
                }
            }
        }
    }

    private static List<Double> renewalCentroids(
        Sport sport, List<List<RankGameParticipant>> teams, List<Double> centroids) {
        List<Double> newCentroids = new ArrayList<>();
        for (List<RankGameParticipant> team : teams) {
            if (team.isEmpty()) {
                newCentroids.add(centroids.get(newCentroids.size()));
            } else {
                double avg = team.stream()
                    .mapToDouble(participant -> participant.getMember().getMmr(sport))
                    .average()
                    .orElse(0);
                newCentroids.add(avg);
            }
        }
        return newCentroids;
    }

    private boolean isConverged(int numTeams, List<Double> newCentroids, List<Double> centroids) {
        double diff = 0;
        for (int teamIndex = 0; teamIndex < numTeams; teamIndex++) {
            diff += Math.abs(newCentroids.get(teamIndex) - centroids.get(teamIndex));
        }
        return diff < CONVERGED_THRESHOLD;
    }
}
