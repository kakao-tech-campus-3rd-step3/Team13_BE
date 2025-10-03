package com.b4f2.pting.matchingsystem.algorithm;

import java.util.List;

import com.b4f2.pting.matchingsystem.model.MatchResult;
import com.b4f2.pting.matchingsystem.model.Player;

public interface MatchingAlgorithm {

    String getName();

    List<MatchResult> match(List<Player> players, int playersPerGame);
}
