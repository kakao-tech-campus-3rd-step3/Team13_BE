package com.b4f2.pting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match_Result_vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public boolean isMemberVote(Member member) {
        return this.member != null && this.member.equals(member);
    }

    public MatchResultVote(Member member, RankGame game, RankGameTeam winTeam) {
        this(null, member, game, winTeam);
    }

    public RankGameTeam getVotedTeam() {
        return winTeam;
    }

    public boolean isWinTeam(RankGameTeam rankGameTeam) {
        return this.winTeam == rankGameTeam;
    }
}
