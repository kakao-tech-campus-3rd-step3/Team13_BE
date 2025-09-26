package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.GameParticipant;
import com.b4f2.pting.domain.GameParticipants;
import com.b4f2.pting.domain.GameReport;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.ReportStatus;
import com.b4f2.pting.dto.GameReportRequest;
import com.b4f2.pting.dto.GameReportResponse;
import com.b4f2.pting.dto.GameReportStatusUpdateRequest;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.GameReportRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GameReportServiceTest {

    @Mock
    private GameReportRepository reportRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameParticipantRepository participantRepository;

    @InjectMocks
    private GameReportService reportService;

    private Member reporter;
    private Member reported;
    private Game game;
    private List<GameParticipant> participants;

    @BeforeEach
    void setUp() {
        reporter = new Member(1L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(reporter, "id", 1L);

        reported = new Member(2L, Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(reported, "id", 2L);

        game = new Game();
        ReflectionTestUtils.setField(game, "id", 1L);
        ReflectionTestUtils.setField(game, "gameStatus", GameStatus.END);

        participants = List.of(
            new GameParticipant(reporter, game),
            new GameParticipant(reported, game)
        );
    }

    @Test
    void createReport_신고하기_성공() {
        // given
        GameReportRequest request = new GameReportRequest(
            game.getId(),
            reported.getId(),
            "테스트 신고 사유"
        );

        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(memberRepository.findById(reported.getId())).thenReturn(Optional.of(reported));
        when(participantRepository.findByGame(game)).thenReturn(participants);
        when(reportRepository.save((GameReport) any(GameReport.class))).thenAnswer(i -> i.getArgument(0));

        // when
        GameReportResponse response = reportService.createReport(reporter, request);

        // then
        assertNotNull(response);
        assertEquals(game.getId(), response.gameId());
        assertEquals(reporter.getId(), response.reporterId());
        assertEquals(reported.getId(), response.reportedId());
        assertEquals("테스트 신고 사유", response.reasonText());
        assertEquals(ReportStatus.PENDING, response.status());
    }

    @Test
    void createReport_자기자신신고_예외발생() {
        // given
        GameReportRequest request = new GameReportRequest(
            game.getId(),
            reporter.getId(),
            "자기 자신 신고"
        );

        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        when(memberRepository.findById(reporter.getId())).thenReturn(Optional.of(reporter));
        when(participantRepository.findByGame(game)).thenReturn(participants);

        // when & then
        assertThatThrownBy(() -> reportService.createReport(reporter, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("자기 자신을 신고할 수 없습니다.");
    }

    @Test
    void updateReportStatus_상태변경_성공() {
        // given
        GameReport report = GameReport.create(game, reporter, reported, "부적절한 행동", new GameParticipants(participants));
        ReflectionTestUtils.setField(report, "id", 1L);

        GameReportStatusUpdateRequest request = new GameReportStatusUpdateRequest(ReportStatus.RESOLVED);

        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));

        // when
        GameReportResponse response = reportService.updateReportStatus(report.getId(), request);

        // then
        assertEquals(ReportStatus.RESOLVED, response.status());
    }

}
