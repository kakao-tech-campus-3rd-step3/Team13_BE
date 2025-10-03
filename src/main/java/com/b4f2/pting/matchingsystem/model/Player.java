package com.b4f2.pting.matchingsystem.model;

import lombok.Getter;

@Getter
public class Player {

    private int id;
    private double mmr;
    private int matchCount;

    public Player(int id, double mmr) {
        this.id = id;
        this.mmr = mmr;
        this.matchCount = 0;
    }

    public void incrementMatchCount() {
        this.matchCount++;
    }
}
