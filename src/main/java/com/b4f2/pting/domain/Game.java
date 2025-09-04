package com.b4f2.pting.domain;

import java.time.LocalDateTime;

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
@Table(name = "game")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column(name = "player_count")
    private Integer playerCount;

    @Column(name = "game_status")
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "duration")
    private Integer duration;

    public enum GameStatus {
        ON_MATCHING,
        END
    }

    public Game(Sport sport, Integer playerCount, GameStatus gameStatus, LocalDateTime startTime, Integer duration) {
        this(null, sport, playerCount, gameStatus, startTime, duration);
    }
}
