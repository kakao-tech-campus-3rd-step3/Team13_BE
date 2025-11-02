package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.Sport;

public class MMRBucketMatching implements MatchingAlgorithm {

    private static final double BUCKET_WIDTH = 2d;

    @Override
    public String getName() {
        return "MMRBucket";
    }

    @Override
    public List<List<RankGameParticipant>> match(List<RankGameParticipant> participants, Sport sport) {
        List<List<RankGameParticipant>> results = new ArrayList<>();
        int playersPerGame = sport.getRecommendedPlayerCount();

        if (participants == null || participants.isEmpty() || participants.size() < playersPerGame) {
            return results; // 인원이 부족해서 매칭시킬 수 없는 경우
        }

        Map<Double, List<RankGameParticipant>> buckets = new TreeMap<>();

        for (RankGameParticipant p : participants) {
            double mmr = p.getMember().getMmr(sport);
            double bucketKey = getBucketKey(mmr);

            buckets.computeIfAbsent(bucketKey, k -> new ArrayList<>()).add(p);
        }

        for (List<RankGameParticipant> bucket : buckets.values()) {

            if (bucket.size() < playersPerGame) {
                continue; // 버킷에 모인 인원으로 매칭을 만들 수 없으면 스킵
            }

            bucket.sort((p1, p2) ->
                    Double.compare(p2.getMember().getMmr(sport), p1.getMember().getMmr(sport)));

            for (int i = 0; i + playersPerGame <= bucket.size(); i += playersPerGame) {
                results.add(new ArrayList<>(bucket.subList(i, i + playersPerGame)));
            }
        }

        return results;
    }

    private double getBucketKey(double mmr) {
        return Math.floor(mmr / BUCKET_WIDTH) * BUCKET_WIDTH;
    }
}
