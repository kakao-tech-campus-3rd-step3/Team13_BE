package com.b4f2.pting.domain;

import java.util.ArrayList;
import java.util.List;

public class GameParticipants {

    private final List<Member> members;

    public GameParticipants(List<Member> members) {
        this.members = new ArrayList<>(members);
    }

    public boolean contains(Long memberId) {
        return members.stream().anyMatch(m -> m.getId().equals(memberId));
    }
}
