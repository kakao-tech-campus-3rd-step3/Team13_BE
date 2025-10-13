package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;

public class BalancedKMeansClusterMatching implements MatchingAlgorithm {

    private final static int MAX_ITER = 100;
    private final static double CONVERGED_THRESHOLD = 1e-4;

    @Override
    public String getName() {
        return "BalancedKMeansCluster";
    }

    @Override
    public List<List<Member>> match(List<Member> players, Sport sport) {
        int teamSize = sport.getRecommendedPlayerCount();

        if (players == null || players.isEmpty() || teamSize <= 0) {
            return List.of();
        }

        int totalPlayers = players.size();
        int numTeams = (int) Math.ceil((double) totalPlayers / teamSize);

        List<List<Member>> teams = new ArrayList<>();

        for (int i = 0; i < numTeams; i++) {
            teams.add(new ArrayList<>());
        }

        List<Double> mmrs = players.stream()
            .map(member -> member.getMmr(sport))
            .toList();

        List<Double> centroids = initialCentroids(mmrs, numTeams);

        for (int iter = 0; iter < MAX_ITER; iter++) {
            allocateMembersToTeams(players, sport, numTeams, centroids, teams, teamSize);

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
        for (int i = 0; i < numTeams; i++) {
            centroids.add(min + (max - min) * i / numTeams);
        }
        return centroids;
    }

    private void allocateMembersToTeams(
        List<Member> players,
        Sport sport,
        int numTeams,
        List<Double> centroids,
        List<List<Member>> teams,
        int teamSize
    ) {
        teams.forEach(List::clear);

        for (Member m : players) {
            double mmr = m.getMmr(sport);

            List<Integer> sortedTeams = new ArrayList<>();
            for (int i = 0; i < numTeams; i++) {
                sortedTeams.add(i);
            }

            sortedTeams.sort(Comparator.comparingDouble(i -> Math.abs(mmr - centroids.get(i))));

            for (int idx : sortedTeams) {
                if (teams.get(idx).size() < teamSize) {
                    teams.get(idx).add(m);
                    break;
                }
            }
        }
    }

    private static List<Double> renewalCentroids(Sport sport, List<List<Member>> teams, List<Double> centroids) {
        List<Double> newCentroids = new ArrayList<>();
        for (List<Member> team : teams) {
            if (team.isEmpty()) {
                newCentroids.add(centroids.get(newCentroids.size()));
            } else {
                double avg = team.stream().mapToDouble(member -> member.getMmr(sport)).average().orElse(0);
                newCentroids.add(avg);
            }
        }
        return newCentroids;
    }

    private boolean isConverged(int numTeams, List<Double> newCentroids, List<Double> centroids) {
        double diff = 0;
        for (int i = 0; i < numTeams; i++) {
            diff += Math.abs(newCentroids.get(i) - centroids.get(i));
        }
        return diff < CONVERGED_THRESHOLD;
    }
}
