package com.b4f2.pting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_user")
@NoArgsConstructor
public class GameUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public GameUser(Long memberId, Game game) {
        this.memberId = memberId;
        this.game = game;
    }
}
