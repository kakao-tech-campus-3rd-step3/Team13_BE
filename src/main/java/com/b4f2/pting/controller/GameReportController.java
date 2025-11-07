package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.GameReportRequest;
import com.b4f2.pting.dto.GameReportResponse;
import com.b4f2.pting.dto.GameReportStatusUpdateRequest;
import com.b4f2.pting.dto.GameReportsResponse;
import com.b4f2.pting.service.GameReportService;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "사용자 신고 API")
public class GameReportController {

    private final GameReportService reportService;

    @PostMapping
    public ResponseEntity<GameReportResponse> createReport(
            @Login Member reporter, @RequestBody GameReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(reporter, request));
    }

    @GetMapping
    public ResponseEntity<GameReportsResponse> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<GameReportsResponse> getReportsByGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(reportService.getReportsByGame(gameId));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<GameReportsResponse> getReportsByReporter(@PathVariable Long memberId) {
        return ResponseEntity.ok(reportService.getReportsByReporter(memberId));
    }

    @PatchMapping("/{reportId}/status")
    public ResponseEntity<GameReportResponse> updateReportStatus(
            @PathVariable Long reportId, @RequestBody GameReportStatusUpdateRequest request) {
        return ResponseEntity.ok(reportService.updateReportStatus(reportId, request));
    }
}
