package com.b4f2.pting.matchingsystem.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.b4f2.pting.matchingsystem.model.MatchResult;
import com.b4f2.pting.matchingsystem.model.Player;

@Component
public class SimpleMMRMatching implements MatchingAlgorithm {

    @Override
    public String getName() {
        return "SimpleMMR";
    }

    @Override
    public List<MatchResult> match(List<Player> players, int playersPerGame) {
        List<MatchResult> results = new ArrayList<>();

        players.sort(Comparator.comparingDouble(Player::getMmr));

        for (int i = 0; i + playersPerGame <= players.size(); i += playersPerGame) {
            results.add(new MatchResult(players.subList(i, i + playersPerGame)));
        }

        return results;
    }
}
