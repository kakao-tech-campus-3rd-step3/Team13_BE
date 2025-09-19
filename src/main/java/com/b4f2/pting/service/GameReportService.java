package com.b4f2.pting.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.GameParticipants;
import com.b4f2.pting.domain.GameReport;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.GameReportRequest;
import com.b4f2.pting.dto.GameReportResponse;
import com.b4f2.pting.dto.GameReportStatusUpdateRequest;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.GameReportRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReportService {

    private final GameReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameParticipantRepository participantRepository;

    @Transactional
    public GameReportResponse createReport(Member reporter, GameReportRequest request) {

        Game game = gameRepository.findById(request.gameId())
            .orElseThrow(() -> new EntityNotFoundException("해당 게임이 존재하지 않습니다."));

        Member reported = memberRepository.findById(request.reportedId())
            .orElseThrow(() -> new IllegalArgumentException("피신고자를 찾을 수 없습니다."));

        List<GameParticipant> members = participantRepository.findByGame(game);
        GameParticipants participants = new GameParticipants(members);

        GameReport report = GameReport.create(game, reporter, reported, request.reasonText(), participants);

        reportRepository.save(report);

        return GameReportResponse.from(report);
    }

    public List<GameReport> getAllReports() {
        return reportRepository.findAll();
    }

    public List<GameReport> getReportsByGame(Long gameId) {
        return reportRepository.findByGameId(gameId);
    }

    public List<GameReport> getReportsByReporter(Long memberId) {
        return reportRepository.findByReporterId(memberId);
    }

    @Transactional
    public GameReportResponse updateReportStatus(Long reportId, GameReportStatusUpdateRequest request) {
        GameReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));

        report.changeStatus(request.status());

        return GameReportResponse.from(report);
    }
}
