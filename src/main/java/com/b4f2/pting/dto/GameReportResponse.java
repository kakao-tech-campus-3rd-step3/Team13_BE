package com.b4f2.pting.dto;

import java.time.LocalDateTime;

import com.b4f2.pting.domain.GameReport.ReportStatus;

public record GameReportResponse(
    Long id,
    Long gameId,
    Long reporterId,
    Long reportedId,
    String reasonText,
    ReportStatus status,
    LocalDateTime createdAt
) {

}
