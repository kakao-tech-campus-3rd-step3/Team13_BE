package com.b4f2.pting.domain;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameParticipants {

    private final List<GameParticipant> participants;

    public boolean checkAlreadyParticipate(long memberId) {
        return participants.stream().anyMatch(gameParticipant ->
            gameParticipant.getMember().getId().equals(memberId)
        );
    }

    public int size() {
        return participants.size();
    }

}
