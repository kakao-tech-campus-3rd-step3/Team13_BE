package com.b4f2.pting.dto;

import com.b4f2.pting.domain.RankGameTeam;

public record VoteRequest(
    RankGameTeam winTeam
) {

}
