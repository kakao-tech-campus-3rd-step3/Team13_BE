package com.b4f2.pting.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "game_report",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "reporter_id", "reported_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GameReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private Member reported;

    @Column(name = "reason_text", nullable = false, length = 255)
    private String reasonText;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ReportStatus {
        PENDING,   // 처리 전
        RESOLVED,  // 처리 완료
        REJECTED   // 기각
    }

    public static GameReport create(Game game, Member reporter, Member reported, String reasonText,
        GameParticipants participants) {
        if (!game.isEnded()) {
            throw new IllegalStateException("게임이 종료된 후에만 신고할 수 있습니다.");
        }
        if (!participants.hasParticipated(reporter)) {
            throw new IllegalArgumentException("신고자는 해당 게임에 참여하지 않았습니다.");
        }
        if (!participants.hasParticipated(reported)) {
            throw new IllegalArgumentException("피신고자는 해당 게임에 참여하지 않았습니다.");
        }
        if (reporter.isEqualMember(reported)) {
            throw new IllegalStateException("자기 자신을 신고할 수 없습니다.");
        }
        return new GameReport(game, reporter, reported, reasonText);
    }

    private GameReport(Game game, Member reporter, Member reported, String reasonText) {
        this.game = game;
        this.reporter = reporter;
        this.reported = reported;
        this.reasonText = reasonText;
    }

    public void changeStatus(ReportStatus status) {
        this.status = status;
    }
}
