package com.b4f2.pting.dto;

import java.util.List;

public record GameReportsResponse(
    List<GameReportResponse> reports
) {

}
