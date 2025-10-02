package com.b4f2.pting.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rank_game")
public class RankGame extends Game {

    @OneToMany(mappedBy = "game")
    private List<MatchResultVote> matchResultVoteList;
}
