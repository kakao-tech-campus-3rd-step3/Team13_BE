package com.b4f2.pting.algorithm;

import java.util.List;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;

public interface MatchingAlgorithm {

    String getName();

    List<List<Member>> match(List<Member> players, Sport sport);
}
