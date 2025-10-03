package com.b4f2.pting.matchingsystem.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.b4f2.pting.matchingsystem.model.Player;

public class TestDataGenerator {

    public List<Player> generatePlayers(int count, double mean, double stddev) {
        long seed = 42L;

        Random random = new Random(seed);
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double gaussian = random.nextGaussian(); // 평균 0, 분산 1
            double mmr = mean + gaussian * stddev;
            players.add(new Player(i, mmr));
        }

        return players;
    }
}
