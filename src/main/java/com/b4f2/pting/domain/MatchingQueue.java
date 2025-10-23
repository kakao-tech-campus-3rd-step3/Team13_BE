package com.b4f2.pting.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class MatchingQueue {

    private final Map<Long, List<RankGameParticipant>> gameQueues = new ConcurrentHashMap<>();

    public void addPlayer(Long sportId, RankGameParticipant participant) {
        participant.joinQueue();
        gameQueues.computeIfAbsent(sportId, k -> Collections.synchronizedList(new ArrayList<>())).add(participant);
    }

    public RankGameParticipants getPlayers(Long sportId) {
        List<RankGameParticipant> gameParticipants = gameQueues.getOrDefault(sportId, new ArrayList<>());
        return new RankGameParticipants(gameParticipants);
    }

    public void removePlayers(Long sportId, List<RankGameParticipant> matched) {
        if (!gameQueues.containsKey(sportId)) {
            // 해당 sportId의 큐가 없으면 아무것도 안 함
            return;
        }

        List<RankGameParticipant> gameParticipants = gameQueues.get(sportId);
        gameParticipants.removeAll(matched);
    }

    public int getQueueSize(Long sportId) {
        List<RankGameParticipant> gameParticipants = gameQueues.getOrDefault(sportId, new ArrayList<>());
        return gameParticipants.size();
    }
}
