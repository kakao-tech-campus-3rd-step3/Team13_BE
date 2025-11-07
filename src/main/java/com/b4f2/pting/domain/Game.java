package com.b4f2.pting.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@Inheritance(strategy = InheritanceType.JOINED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;

    @Column(name = "game_location")
    private String gameLocation;

    @Column(name = "player_count")
    private Integer playerCount;

    @Column(name = "game_status")
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    public enum GameStatus {
        ON_RECRUITING, // 모집 중
        FULL, // 모집 완료 (인원 다 참)
        CLOSED, // 모집 종료
        END, // 게임 종료
        CANCELED // 게임 취소
    ;

        public boolean isOnRecruiting() {
            return this == GameStatus.ON_RECRUITING || this == GameStatus.FULL;
        }
    }

    public static Game create(
            Sport sport,
            String gameLocation,
            Integer playerCount,
            GameStatus gameStatus,
            LocalDateTime startTime,
            Integer duration,
            String description,
            String imageUrl) {
        LocalDateTime nowInSeoul = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (startTime.isBefore(nowInSeoul)) {
            throw new IllegalArgumentException("매치 시작 시간은 현재 시간보다 이후여야 합니다.");
        }

        return new Game(null, sport, gameLocation, playerCount, gameStatus, startTime, duration, description, imageUrl);
    }

    public boolean isStatus(GameStatus status) {
        return this.gameStatus == status;
    }

    public boolean isEnded() {
        return this.gameStatus == GameStatus.END;
    }

    public void changeStatus(GameStatus status) {
        this.gameStatus = status;
    }
}
