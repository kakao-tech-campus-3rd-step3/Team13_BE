package com.b4f2.pting.domain;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameParticipants {

    private final List<GameParticipant> participants;

    public boolean hasParticipated(Member member) {
        return participants.stream().anyMatch(gameParticipant ->
            gameParticipant.getMember().isEqualMember(member)
        );
    }

    public int size() {
        return participants.size();
    }

}
