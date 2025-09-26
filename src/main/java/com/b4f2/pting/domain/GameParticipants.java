package com.b4f2.pting.domain;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameParticipants {

    private final List<GameParticipant> participants;

    public int size() {
        return participants.size();
    }

    public void validateParticipated(Member member) {
        if (!hasParticipated(member)) {
            throw new IllegalArgumentException(member.getName() + "은/는 해당 게임에 참여하지 않았습니다.");
        }
    }

    public void validateNotParticipated(Member member) {
        if (hasParticipated(member)) {
            throw new IllegalStateException(member.getName() + "은/는 이미 게임에 참여했습니다.");
        }
    }

    private boolean hasParticipated(Member member) {
        return participants.stream().anyMatch(gameParticipant ->
            gameParticipant.getMember().isEqualMember(member)
        );
    }
}
