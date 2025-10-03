package com.b4f2.pting.matchingsystem.model;

import java.util.List;

import lombok.Getter;

@Getter
public class MatchResult {

    private List<Player> match;

    public MatchResult(List<Player> match) {
        this.match = match;
        for (Player player : match) {
            player.incrementMatchCount();
        }
    }
}
