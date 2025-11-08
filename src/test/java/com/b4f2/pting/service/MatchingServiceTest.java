package com.b4f2.pting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.b4f2.pting.algorithm.MatchingAlgorithm;
import com.b4f2.pting.domain.FcmToken;
import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.MatchingQueue;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.RankGame;
import com.b4f2.pting.domain.RankGameParticipant;
import com.b4f2.pting.domain.RankGameParticipants;
import com.b4f2.pting.domain.RankGameTeam;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.RankGameConfirmRequest;
import com.b4f2.pting.dto.RankGameEnqueueRequest;
import com.b4f2.pting.repository.FcmTokenRepository;
import com.b4f2.pting.repository.RankGameParticipantRepository;
import com.b4f2.pting.repository.RankGameRepository;
import com.b4f2.pting.repository.SportRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MatchingServiceTest {

    @Mock
    private MatchingQueue matchingQueue;

    @Mock
    private RankGameRepository rankGameRepository;

    @Mock
    private RankGameParticipantRepository rankGameParticipantRepository;

    @Mock
    private MatchingAlgorithm matchingAlgorithm;

    @Mock
    private FcmService fcmService;

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @Mock
    private SportRepository sportRepository;

    @InjectMocks
    private MatchingService matchingService;

    private Member member;
    private Sport sport;

    @BeforeEach
    void setUp() {
        member = new Member("Id", Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member, "id", 1L);

        sport = new Sport();
        ReflectionTestUtils.setField(sport, "id", 1L);
        ReflectionTestUtils.setField(sport, "name", "축구");
    }

    // -------------------------------
    // addPlayerToQueue()
    // -------------------------------
    @Test
    void addPlayerToQueue_랭크게임참가_성공() {
        // given
        RankGameEnqueueRequest request = new RankGameEnqueueRequest(1L);
        when(sportRepository.existsById(1L)).thenReturn(true);
        when(rankGameParticipantRepository.save(any(RankGameParticipant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        RankGameParticipant result = matchingService.addPlayerToQueue(member, request);

        // then
        assertThat(result).isNotNull();
        verify(matchingQueue).addPlayer(eq(1L), any(RankGameParticipant.class));
        verify(rankGameParticipantRepository).save(any(RankGameParticipant.class));
    }

    @Test
    void addPlayerToQueue_존재하지않는스포츠_예외발생() {
        // given
        when(sportRepository.existsById(anyLong())).thenReturn(false);
        RankGameEnqueueRequest request = new RankGameEnqueueRequest(99L);

        // when & then
        assertThatThrownBy(() -> matchingService.addPlayerToQueue(member, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 스포츠가 존재하지 않습니다.");
    }

    // -------------------------------
    // proposeGamesFromQueue()
    // -------------------------------
    @Test
    void proposeGamesFromQueue_최소인원미달_빈리스트반환() {
        // given
        RankGameParticipants queue = mock(RankGameParticipants.class);
        when(queue.getGameParticipantList()).thenReturn(List.of());
        when(matchingQueue.getPlayers(sport.getId())).thenReturn(queue);
        ReflectionTestUtils.setField(sport, "recommendedPlayerCount", 5);

        // when
        List<RankGame> result = matchingService.proposeGamesFromQueue(sport);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void proposeGamesFromQueue_인원충족_성공() throws Exception {
        // given
        Sport sport1 = new Sport();
        ReflectionTestUtils.setField(sport1, "id", 2L);
        ReflectionTestUtils.setField(sport1, "name", "축구");
        ReflectionTestUtils.setField(sport1, "recommendedPlayerCount", 2); // 최소 인원 2명

        // 참가자 mock
        RankGameParticipant participant1 = mock(RankGameParticipant.class);
        RankGameParticipant participant2 = mock(RankGameParticipant.class);

        Member member1 = new Member("1L", Member.OAuthProvider.KAKAO);
        Member member2 = new Member("2L", Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member1, "id", 1L);
        ReflectionTestUtils.setField(member2, "id", 2L);

        when(participant1.getMember()).thenReturn(member1);
        when(participant2.getMember()).thenReturn(member2);

        List<RankGameParticipant> participantList = new ArrayList<>();
        participantList.add(participant1);
        participantList.add(participant2);

        // matchingQueue.getPlayers()가 RankGameParticipants 반환
        RankGameParticipants queue = mock(RankGameParticipants.class);
        when(queue.getGameParticipantList()).thenReturn(participantList);
        when(matchingQueue.getPlayers(sport1.getId())).thenReturn(queue);

        // matchingAlgorithm.match()가 가변 리스트로 반환
        when(matchingAlgorithm.match(anyList(), eq(sport1))).thenReturn(List.of(new ArrayList<>(participantList)));

        // participant assignGame, assignTeam는 void 메서드이므로 doNothing
        doNothing().when(participant1).assignGame(any(RankGame.class));
        doNothing().when(participant1).assignTeam(any(RankGameTeam.class));
        doNothing().when(participant2).assignGame(any(RankGame.class));
        doNothing().when(participant2).assignTeam(any(RankGameTeam.class));

        // --------------------------
        // FCM 관련 stub 추가
        // --------------------------
        FcmToken token1 = new FcmToken(member1, "token1");
        FcmToken token2 = new FcmToken(member2, "token2");
        when(fcmTokenRepository.findAllByMemberIn(List.of(member1, member2))).thenReturn(List.of(token1, token2));

        // fcmService 호출 무시
        doNothing().when(fcmService).sendMulticastPush(anyList(), anyString(), anyString());

        // when
        List<RankGame> result = matchingService.proposeGamesFromQueue(sport1);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getSport()).isEqualTo(sport1);

        // 참가자 게임/팀 지정 확인
        verify(participant1).assignGame(any(RankGame.class));
        verify(participant2).assignGame(any(RankGame.class));
        verify(participant1).assignTeam(any(RankGameTeam.class));
        verify(participant2).assignTeam(any(RankGameTeam.class));

        // FCM 푸시 호출 확인
        verify(fcmService).sendMulticastPush(anyList(), contains("일정 안내"), anyString());
    }

    // -------------------------------
    // acceptTeam()
    // -------------------------------
    // TODO: 뭐가 문젤까?
    @Test
    void acceptTeam_참가자모두수락_성공() throws Exception {
        // ----------------------------
        // given
        // ----------------------------
        Sport sport = new Sport();
        ReflectionTestUtils.setField(sport, "id", 3L);
        ReflectionTestUtils.setField(sport, "name", "농구");

        RankGame game = new RankGame();
        ReflectionTestUtils.setField(game, "id", 10L);
        ReflectionTestUtils.setField(game, "sport", sport);
        ReflectionTestUtils.setField(game, "gameStatus", Game.GameStatus.ON_RECRUITING);
        ReflectionTestUtils.setField(game, "startTime", LocalDateTime.of(2025, 11, 2, 15, 0));

        // Members 생성 및 ID 세팅
        Member member1 = new Member("2L", Member.OAuthProvider.KAKAO);
        Member member2 = new Member("3L", Member.OAuthProvider.KAKAO);
        ReflectionTestUtils.setField(member1, "id", 2L);
        ReflectionTestUtils.setField(member2, "id", 3L);

        // Participants spy 생성
        RankGameParticipant participant1 = spy(new RankGameParticipant(member1));
        RankGameParticipant participant2 = spy(new RankGameParticipant(member2));
        ReflectionTestUtils.setField(participant1, "team", RankGameTeam.RED_TEAM);
        ReflectionTestUtils.setField(participant2, "team", RankGameTeam.BLUE_TEAM);

        // accept() 실제 호출
        doCallRealMethod().when(participant1).accept();
        doCallRealMethod().when(participant2).accept();

        // ----------------------------
        // repository stubbing
        // ----------------------------
        when(rankGameParticipantRepository.findByGameIdAndMemberId(1L, member1.getId()))
                .thenReturn(Optional.of(participant1));
        when(rankGameParticipantRepository.findByGameIdAndMemberId(1L, member2.getId()))
                .thenReturn(Optional.of(participant2));
        when(rankGameRepository.findById(1L)).thenReturn(Optional.of(game));
        when(rankGameParticipantRepository.findAllByGame(game)).thenReturn(List.of(participant1, participant2));

        // FCM 토큰 조회 Stub
        FcmToken token1 = new FcmToken(member1, "token1");
        FcmToken token2 = new FcmToken(member2, "token2");
        when(fcmTokenRepository.findAllByMemberIn(List.of(member1, member2))).thenReturn(List.of(token1, token2));

        // FCM 푸시 호출 무시
        doNothing().when(fcmService).sendMulticastPush(anyList(), anyString(), anyString());

        // ----------------------------
        // when
        // ----------------------------
        matchingService.acceptTeam(member1, new RankGameConfirmRequest(1L, true));
        matchingService.acceptTeam(member2, new RankGameConfirmRequest(1L, true));

        // ----------------------------
        // then
        // ----------------------------
        // 참가자 accept 확인
        assertThat(participant1.isAccepted()).isTrue();
        assertThat(participant2.isAccepted()).isTrue();

        // 게임 상태 FULL 변경 확인
        assertThat(game.getGameStatus()).isEqualTo(Game.GameStatus.FULL);
        verify(rankGameRepository, atLeastOnce()).save(game);

        // participant 전체 저장 확인
        verify(rankGameParticipantRepository, atLeastOnce()).saveAll(List.of(participant1, participant2));

        // matchingQueue 제거 확인
        verify(matchingQueue).removePlayers(eq(sport.getId()), eq(List.of(participant1, participant2)));

        // FCM 푸시 호출 확인
        verify(fcmService).sendMulticastPush(anyList(), contains("매칭 완료"), anyString());
    }

    @Test
    void acceptTeam_존재하지않는참가자_예외발생() {
        // given
        when(rankGameParticipantRepository.findByGameIdAndMemberId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        RankGameConfirmRequest request = new RankGameConfirmRequest(1L, Boolean.TRUE);

        // when & then
        assertThatThrownBy(() -> matchingService.acceptTeam(member, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("참가자를 찾을 수 없습니다.");
    }
}
