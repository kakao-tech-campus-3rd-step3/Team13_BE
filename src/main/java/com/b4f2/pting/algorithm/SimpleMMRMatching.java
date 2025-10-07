package com.b4f2.pting.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;

@Component
public class SimpleMMRMatching implements MatchingAlgorithm {

    @Override
    public String getName() {
        return "SimpleMMR";
    }

    @Override
    public List<List<Member>> match(List<Member> players, Sport sport) {
        List<List<Member>> results = new ArrayList<>();

        int playersPerGame = sport.getRecommendedPlayerCount();

        players.sort(Comparator.comparingDouble(m -> m.getMmr(sport)));

        for (int i = 0; i + playersPerGame < players.size(); i += playersPerGame) {
            results.add(players.subList(i, i + playersPerGame));
        }

        return results;
    }
}
