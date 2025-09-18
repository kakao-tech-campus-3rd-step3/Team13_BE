package com.b4f2.pting.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Game.GameStatus;
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

        if (!game.getGameStatus().equals(GameStatus.END)) {
            throw new IllegalStateException("게임이 종료된 후에만 신고할 수 있습니다.");
        }

        Member reported = memberRepository.findById(request.reportedId())
            .orElseThrow(() -> new IllegalArgumentException("피신고자를 찾을 수 없습니다."));

        List<Long> participants = participantRepository.findMemberIdsByGameId(request.gameId());

        if (!participants.contains(reporter.getId())) {
            throw new IllegalArgumentException("신고자는 해당 게임에 참여하지 않았습니다.");
        }
        if (!participants.contains(request.reportedId())) {
            throw new IllegalArgumentException("피신고자는 해당 게임에 참여하지 않았습니다.");
        }
        if (reporter.getId().equals((request.reportedId()))) {
            throw new IllegalStateException("자기 자신을 신고할 수 없습니다.");
        }

        GameReport report = new GameReport(game, reporter, reported, request.reasonText());

        reportRepository.save(report);

        return new GameReportResponse(
            report.getId(),
            report.getGame().getId(),
            report.getReporter().getId(),
            report.getReported().getId(),
            report.getReasonText(),
            report.getStatus(),
            report.getCreatedAt()
        );
    }

    public List<GameReport> getAllReports() {
        return reportRepository.findAll();
    }

    public List<GameReport> getReportsByGame(Long gameId) {
        return reportRepository.findByGameId(gameId);
    }

    public List<GameReport> getReportsByMember(Long memberId) {
        return reportRepository.findByReporterId(memberId);
    }

    @Transactional
    public GameReportResponse updateReportStatus(Long reportId, GameReportStatusUpdateRequest request) {
        GameReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));

        report.changeStatus(request.status());

        return new GameReportResponse(
            report.getId(),
            report.getGame().getId(),
            report.getReporter().getId(),
            report.getReported().getId(),
            report.getReasonText(),
            report.getStatus(),
            report.getCreatedAt()
        );
    }
}
