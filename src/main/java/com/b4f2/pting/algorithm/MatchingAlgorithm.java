package com.b4f2.pting.algorithm;

import java.util.List;

import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.Sport;

public interface MatchingAlgorithm {

    String getName();

    List<List<RankGameParticipant>> match(List<RankGameParticipant> participants, Sport sport);
}
