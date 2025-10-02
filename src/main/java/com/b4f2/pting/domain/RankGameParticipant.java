package com.b4f2.pting.domain;

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
}
