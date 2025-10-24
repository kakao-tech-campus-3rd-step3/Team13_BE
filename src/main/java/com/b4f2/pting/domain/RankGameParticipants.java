package com.b4f2.pting.domain;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RankGameParticipants {

    private final List<RankGameParticipant> gameParticipantList;
}
