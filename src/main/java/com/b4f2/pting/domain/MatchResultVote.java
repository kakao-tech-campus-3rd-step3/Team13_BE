package com.b4f2.pting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "match_Result_vote")
public class MatchResultVote {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private RankGame game;

    @Column(name = "win_team")
    @Enumerated(EnumType.STRING)
    private RankGameTeam winTeam;
}
