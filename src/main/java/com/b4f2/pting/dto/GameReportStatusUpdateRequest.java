package com.b4f2.pting.dto;

import com.b4f2.pting.domain.GameReport.ReportStatus;

public record GameReportStatusUpdateRequest(
    ReportStatus status
) {

}
