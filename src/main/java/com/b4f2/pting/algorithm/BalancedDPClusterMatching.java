package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.Sport;

/**
 * BalancedDPCluster
 * - 멤버를 MMR 오름차순 정렬 후, 팀 크기 s로 가득 찬 팀 T=⌊n/s⌋개를 전역 최적으로 구성
 * - 남는 r=n-T·s 명은 어디서 제외할지까지 DP로 최적화
 * - 목표: 팀 내 SSE(분산×팀원수) 합 최소화
 */
public final class BalancedDPClusterMatching implements MatchingAlgorithm {

    @Override
    public String getName() {
        return "BalancedDPCluster";
    }

    @Override
    public List<List<RankGameParticipant>> match(final List<RankGameParticipant> participants, final Sport sport) {
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        final int matchSize = sport.getRecommendedPlayerCount();
        if (matchSize <= 0) {
            return List.of();
        }

        final var sorted = sortParticipantsByMmr(participants, sport);

        final int n = sorted.size();
        final int matchCount = n / matchSize;
        if (matchCount == 0) {
            return List.of();
        }

        final int maxSkips = n - matchCount * matchSize;

        final PrefixSums prefix = PrefixSums.of(sorted);
        final BiFunction<Integer, Integer, Double> sse = prefix::sse;

        final DPResult dpResult = runDP(n, matchCount, maxSkips, matchSize, sse);

        final int bestK = findBestK(dpResult, n, matchCount, maxSkips);
        if (bestK == -1) {
            return List.of();
        }

        return reconstructMatches(dpResult, sorted, n, matchCount, bestK, matchSize);
    }

    private List<ParticipantWithMmr> sortParticipantsByMmr(
            final List<RankGameParticipant> participants, final Sport sport) {
        return participants.stream()
                .map(p -> new ParticipantWithMmr(p, p.getMember().getMmr(sport)))
                .sorted(Comparator.comparingDouble(ParticipantWithMmr::mmr))
                .toList();
    }

    private DPResult runDP(
            final int n,
            final int matchCount,
            final int maxSkips,
            final int matchSize,
            final BiFunction<Integer, Integer, Double> sse) {
        final double INF = Double.POSITIVE_INFINITY;

        final double[][][] dp = new double[n + 1][matchCount + 1][maxSkips + 1];
        final int[][][] prevI = new int[n + 1][matchCount + 1][maxSkips + 1];
        final int[][][] prevK = new int[n + 1][matchCount + 1][maxSkips + 1];
        final Action[][][] act = new Action[n + 1][matchCount + 1][maxSkips + 1];

        for (int i = 0; i <= n; i++) {
            for (int t = 0; t <= matchCount; t++) {
                Arrays.fill(dp[i][t], INF);
            }
        }
        dp[0][0][0] = 0.0;

        for (int i = 1; i <= n; i++) {
            final int maxMatchesByI = Math.min(matchCount, i / matchSize);
            final int maxSkipsByI = Math.min(maxSkips, i);
            for (int t = 0; t <= maxMatchesByI; t++) {
                for (int k = 0; k <= maxSkipsByI; k++) {
                    // (A) i번째 참가자 제외
                    if (k > 0 && dp[i - 1][t][k - 1] < dp[i][t][k]) {
                        dp[i][t][k] = dp[i - 1][t][k - 1];
                        prevI[i][t][k] = i - 1;
                        prevK[i][t][k] = k - 1;
                        act[i][t][k] = Action.SKIP;
                    }

                    // (B) [i - matchSize + 1 .. i] 로 매치 하나 완성(연속 구간)
                    if (t > 0 && i >= matchSize) {
                        final double prev = dp[i - matchSize][t - 1][k];
                        if (prev < INF) {
                            final int l = i - matchSize + 1;
                            final double cand = prev + sse.apply(l, i);
                            if (cand < dp[i][t][k]) {
                                dp[i][t][k] = cand;
                                prevI[i][t][k] = i - matchSize;
                                prevK[i][t][k] = k;
                                act[i][t][k] = Action.MATCH;
                            }
                        }
                    }
                }
            }
        }
        return new DPResult(dp, prevI, prevK, act);
    }

    private int findBestK(final DPResult dpResult, final int n, final int matchCount, final int maxSkips) {
        int bestK = 0;
        double best = dpResult.dp[n][matchCount][0];
        for (int k = 1; k <= maxSkips; k++) {
            if (dpResult.dp[n][matchCount][k] < best) {
                best = dpResult.dp[n][matchCount][k];
                bestK = k;
            }
        }
        return Double.isFinite(best) ? bestK : -1;
    }

    private List<List<RankGameParticipant>> reconstructMatches(
            final DPResult dpResult,
            final List<ParticipantWithMmr> sorted,
            final int n,
            final int matchCount,
            final int bestK,
            final int matchSize) {
        final var segments = new ArrayList<Range>();
        for (int i = n, t = matchCount, k = bestK; t > 0; ) {
            if (dpResult.act[i][t][k] == Action.MATCH) {
                final int pi = dpResult.prevI[i][t][k];
                segments.add(new Range(pi + 1, i));
                i = pi;
                t--;
            } else if (dpResult.act[i][t][k] == Action.SKIP) {
                final int pi = dpResult.prevI[i][t][k];
                final int pk = dpResult.prevK[i][t][k];
                i = pi;
                k = pk;
            } else {
                break;
            }
        }
        Collections.reverse(segments);

        final List<List<RankGameParticipant>> matchesOut = new ArrayList<>(segments.size());
        for (Range seg : segments) {
            final var match = new ArrayList<RankGameParticipant>(matchSize);
            for (int idx = seg.l(); idx <= seg.r(); idx++) {
                match.add(sorted.get(idx - 1).participant());
            }
            matchesOut.add(match);
        }
        return List.copyOf(matchesOut);
    }

    private record DPResult(double[][][] dp, int[][][] prevI, int[][][] prevK, Action[][][] act) {}

    private enum Action {
        MATCH,
        SKIP
    }

    private record ParticipantWithMmr(RankGameParticipant participant, double mmr) {}

    private record Range(int l, int r) {}

    /**
     * 1-based prefix sums for SSE computation:
     * SSE(l..r) = sum2(l..r) - (sum(l..r)^2) / m
     */
    private static final class PrefixSums {
        private final double[] a; // 1..n
        private final double[] ps; // prefix sum
        private final double[] qs; // prefix sum of squares

        private PrefixSums(double[] a, double[] ps, double[] qs) {
            this.a = a;
            this.ps = ps;
            this.qs = qs;
        }

        static PrefixSums of(List<ParticipantWithMmr> sorted) {
            final int n = sorted.size();
            final double[] a = new double[n + 1];
            final double[] ps = new double[n + 1];
            final double[] qs = new double[n + 1];
            for (int i = 1; i <= n; i++) {
                a[i] = sorted.get(i - 1).mmr();
                ps[i] = ps[i - 1] + a[i];
                qs[i] = qs[i - 1] + a[i] * a[i];
            }
            return new PrefixSums(a, ps, qs);
        }

        double sse(int l, int r) {
            final double sum = ps[r] - ps[l - 1];
            final double sum2 = qs[r] - qs[l - 1];
            final int m = r - l + 1;
            return sum2 - (sum * sum) / m;
        }
    }
}
