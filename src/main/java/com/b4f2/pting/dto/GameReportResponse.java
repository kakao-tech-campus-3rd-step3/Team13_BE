package com.b4f2.pting.dto;

import java.time.LocalDateTime;

import com.b4f2.pting.domain.GameReport;
import com.b4f2.pting.domain.ReportStatus;

public record GameReportResponse(
        Long id,
        Long gameId,
        Long reporterId,
        Long reportedId,
        String reasonText,
        ReportStatus status,
        LocalDateTime createdAt) {

    public static GameReportResponse from(GameReport report) {
        return new GameReportResponse(
                report.getId(),
                report.getGame().getId(),
                report.getReporter().getId(),
                report.getReported().getId(),
                report.getReasonText(),
                report.getStatus(),
                report.getCreatedAt());
    }
}
