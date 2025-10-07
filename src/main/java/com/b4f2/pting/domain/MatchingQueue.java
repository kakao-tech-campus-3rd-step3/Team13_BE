package com.b4f2.pting.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MatchingQueue {

    private final Map<Long, List<Member>> gameQueues = new HashMap<>();

    public void addPlayer(Long sportId, Member member) {
        gameQueues.computeIfAbsent(sportId, k -> new ArrayList<>()).add(member);
    }

    public List<Member> getPlayers(Long sportId) {
        return gameQueues.getOrDefault(sportId, new ArrayList<>());
    }

    public void removePlayers(Long sportId, List<Member> matched) {
        gameQueues.getOrDefault(sportId, new ArrayList<>()).removeAll(matched);
    }

    public int getQueueSize(Long sportId) {
        return gameQueues.getOrDefault(sportId, new ArrayList<>()).size();
    }
}
