package com.b4f2.pting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_user")
@NoArgsConstructor
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class GameParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    public GameParticipant(Member member, Game game) {
        this.member = member;
        this.game = game;
    }

    public boolean isEqualMember(Member member) {
        return this.member.isEqualMember(member);
    }

    public void assignGame(Game game) {
        this.game = game;
    }
}
