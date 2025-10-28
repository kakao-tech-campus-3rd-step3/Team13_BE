package com.b4f2.pting.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "rank_game_user")
@NoArgsConstructor
public class RankGameParticipant extends GameParticipant {

    @Column(name = "team")
    @Enumerated(EnumType.STRING)
    private RankGameTeam team;

    @Column(name = "queue_joined_at")
    private LocalDateTime queueJoinedAt;

    @Column(name = "accepted")
    private boolean accepted = false;

    public RankGameParticipant(Member member) {
        super(member, null);
    }

    public boolean isTeam(RankGameTeam rankGameTeam) {
        return team == rankGameTeam;
    }

    public void assignTeam(RankGameTeam team) {
        this.team = team;
    }

    public void joinQueue() {
        this.queueJoinedAt = LocalDateTime.now();
    }

    public void accept() {
        accepted = true;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
