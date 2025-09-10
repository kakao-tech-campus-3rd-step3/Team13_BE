package com.b4f2.pting.domain;

import jakarta.persistence.*;

import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_user")
@NoArgsConstructor
public class GameParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public GameParticipant(Member member, Game game) {
        this.member = member;
        this.game = game;
    }
}
