package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.Sport;

public class SimpleMMRMatching implements MatchingAlgorithm {

    @Override
    public String getName() {
        return "SimpleMMR";
    }

    @Override
    public List<List<RankGameParticipant>> match(List<RankGameParticipant> participants, Sport sport) {
        List<List<RankGameParticipant>> results = new ArrayList<>();

        int playersPerGame = sport.getRecommendedPlayerCount();

        if (participants == null || participants.isEmpty()) {
            return results;
        }

        participants.sort((p1, p2) ->
                Double.compare(p2.getMember().getMmr(sport), p1.getMember().getMmr(sport)));

        for (int i = 0; i + playersPerGame <= participants.size(); i += playersPerGame) {
            results.add(new ArrayList<>(participants.subList(i, i + playersPerGame)));
        }

        return results;
    }
}
