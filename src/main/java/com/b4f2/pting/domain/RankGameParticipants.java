package com.b4f2.pting.domain;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RankGameParticipants {

    private final List<RankGameParticipant> gameParticipantList;

    public List<RankGameParticipant> getGameParticipantList() {
        return gameParticipantList;
    }
}
