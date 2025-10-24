package com.b4f2.pting.dto;

import java.util.List;

import com.b4f2.pting.domain.RankGameTeam;

public record VoteResultResponse(List<RankGameTeam> votedTeams) {}
