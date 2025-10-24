package com.b4f2.pting.dto;

public record GameReportRequest(Long gameId, Long reportedId, String reasonText) {}
