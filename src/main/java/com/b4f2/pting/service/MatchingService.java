package com.b4f2.pting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.algorithm.MatchingAlgorithm;
import com.b4f2.pting.domain.MatchingQueue;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final MatchingQueue matchingQueue;

    public void addPlayerToQueue(Long sportId, Member member) {
        matchingQueue.addPlayer(sportId, member);
    }

    public List<Member> matching(Sport sport, MatchingAlgorithm algorithm) {
        Long sportId = sport.getId();

        List<Member> candidates = matchingQueue.getPlayers(sportId);

        List<List<Member>> matchedGroups = algorithm.match(candidates, sport);

        if (matchedGroups.isEmpty()) {
            return List.of();
        }

        // 첫 번째 매칭 그룹만 뽑는다고 가정
        List<Member> selectedPlayers = matchedGroups.getFirst();

        matchingQueue.removePlayers(sportId, selectedPlayers);

        return selectedPlayers;
    }
}
