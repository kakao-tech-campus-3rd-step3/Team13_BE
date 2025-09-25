package com.b4f2.pting.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.b4f2.pting.config.TestContainersConfig;
import com.b4f2.pting.domain.Game;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.Sport;
import com.b4f2.pting.dto.CreateGameRequest;
import com.b4f2.pting.dto.GameDetailResponse;
import com.b4f2.pting.dto.GamesResponse;
import com.b4f2.pting.repository.GameParticipantRepository;
import com.b4f2.pting.repository.GameRepository;
import com.b4f2.pting.repository.MemberRepository;
import com.b4f2.pting.repository.SportRepository;
import com.b4f2.pting.util.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestContainersConfig.class)
class GameIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameParticipantRepository gameParticipantRepository;

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Member testMember;
    private Sport testSport;
    private String token;

    @BeforeEach
    void setUp() {
        gameParticipantRepository.deleteAll();
        gameRepository.deleteAll();
        memberRepository.deleteAll();
        sportRepository.deleteAll();

        testMember = memberRepository.save(new Member(12345L, Member.OAuthProvider.KAKAO));
        testMember.markAsVerified();
        memberRepository.save(testMember);
        token = jwtUtil.createToken(testMember);

        testSport = sportRepository.save(new Sport("축구", 10));
    }

    private HttpHeaders createAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @DisplayName("게임 생성 성공")
    void createGame_성공() {
        CreateGameRequest request = new CreateGameRequest(
            testSport.getId(),
            "축구 매치",
            10,
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1),
            2,
            "친구들과 함께하는 축구 경기"
        );

        ResponseEntity<GameDetailResponse> response = restTemplate.postForEntity(
            "/api/v1/games",
            new HttpEntity<>(request, createAuthHeader()),
            GameDetailResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("축구 매치");
        assertThat(gameRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("게임 참가 성공")
    void joinGame_성공() {
        Game game = Game.create(
            testSport,
            "축구 매치",
            10,
            Game.GameStatus.ON_MATCHING,
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1),
            2,
            "친구들과 함께하는 축구 경기"
        );
        gameRepository.save(game);

        Member newParticipant = memberRepository.save(new Member(67890L, Member.OAuthProvider.KAKAO));
        newParticipant.markAsVerified();
        memberRepository.save(newParticipant);

        String url = "/api/v1/games/" + game.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.createToken(newParticipant));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            requestEntity,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("종목별 게임 목록 조회")
    void getGamesBySport_성공() {
        Game game1 = Game.create(
            testSport,
            "축구 매치 1",
            10,
            Game.GameStatus.ON_MATCHING,
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1),
            2,
            "친구들과 함께하는 축구 경기"
        );
        gameRepository.save(game1);

        Game game2 = Game.create(
            testSport,
            "축구 매치 2",
            10,
            Game.GameStatus.ON_MATCHING,
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1),
            2,
            "친구들과 함께하는 축구 경기"
        );
        gameRepository.save(game2);

        ResponseEntity<GamesResponse> response = restTemplate.exchange(
            "/api/v1/games?sportId=" + testSport.getId(),
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeader()),
            GamesResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().games()).hasSize(2);
    }

    @Test
    @DisplayName("게임 상세 조회 성공")
    void getGameById_success() {
        Game game = Game.create(
            testSport,
            "축구 매치",
            10,
            Game.GameStatus.ON_MATCHING,
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1),
            2,
            "친구들과 함께하는 축구 경기"
        );
        gameRepository.save(game);

        ResponseEntity<GameDetailResponse> response = restTemplate.exchange(
            "/api/v1/games/" + game.getId(),
            HttpMethod.GET,
            new HttpEntity<>(createAuthHeader()),
            GameDetailResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("축구 매치");
    }
}

