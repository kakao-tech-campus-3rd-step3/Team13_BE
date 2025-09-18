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

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @ManyToOne
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

    public GameReport(Game game, Member reporter, Member reported, String reasonText) {
        this.game = game;
        this.reporter = reporter;
        this.reported = reported;
        this.reasonText = reasonText;
    }

    public void changeStatus(ReportStatus status) {
        this.status = status;
    }
}
