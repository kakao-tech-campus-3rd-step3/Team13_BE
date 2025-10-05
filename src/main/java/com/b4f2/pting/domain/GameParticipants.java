package com.b4f2.pting.domain;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameParticipants {

    private final List<GameParticipant> participants;

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
            gameParticipant.isEqualMember(member)
        );
    }

    public void validateCapacity(Game game) {
        if (size() >= game.getPlayerCount()) {
            throw new IllegalStateException("모집 인원이 마감되었습니다.");
        }
    }

    private int size() {
        return participants.size();
    }
}
