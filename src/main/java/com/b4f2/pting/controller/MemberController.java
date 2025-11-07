package com.b4f2.pting.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Game.GameStatus;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.CertificationRequest;
import com.b4f2.pting.dto.CertificationResponse;
import com.b4f2.pting.dto.CertificationVerifyRequest;
import com.b4f2.pting.dto.ChangeProfileDescriptionRequest;
import com.b4f2.pting.dto.ChangeProfileImageUrlRequest;
import com.b4f2.pting.dto.ChangeProfileNameRequest;
import com.b4f2.pting.dto.GamesResponse;
import com.b4f2.pting.dto.ProfileResponse;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.facade.CertificationService;
import com.b4f2.pting.service.GameService;
import com.b4f2.pting.service.ProfileService;
import com.b4f2.pting.service.SchoolService;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "각종 회원관련 기능 API")
public class MemberController {

    private final CertificationService certificationService;
    private final SchoolService schoolService;
    private final GameService gameService;
    private final ProfileService profileService;

    @PostMapping("/me/school/{schoolId}")
    public ResponseEntity<SchoolResponse> selectSchool(
            @Parameter(hidden = true) @Login Member member, @PathVariable Long schoolId) {
        School school = schoolService.selectSchool(member, schoolId);
        SchoolResponse response = new SchoolResponse(school.getId(), school.getName(), school.getPostfix());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/certification/email")
    public ResponseEntity<String> sendCertificationEmail(
            @Parameter(hidden = true) @Login Member member, @RequestBody CertificationRequest request) {
        certificationService.sendCertificationEmail(member, request);
        return ResponseEntity.ok("인증 메일을 발송했습니다. 메일을 확인하세요.");
    }

    @PostMapping("/me/certification/verify")
    public ResponseEntity<CertificationResponse> verifyCertification(
            @Parameter(hidden = true) @Login Member member, @RequestBody @Valid CertificationVerifyRequest request) {
        CertificationResponse response = certificationService.verifyCertification(member, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/certification/status")
    public ResponseEntity<CertificationResponse> checkCertification(@Parameter(hidden = true) @Login Member member) {
        CertificationResponse response = certificationService.checkCertification(member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/games")
    public ResponseEntity<GamesResponse> getGames(
            @Parameter(hidden = true) @Login Member member, @RequestParam(required = false) GameStatus query) {
        if (query == null) {
            return ResponseEntity.ok(gameService.findGamesByMember(member));
        }
        return ResponseEntity.ok(gameService.findGamesByMemberAndGameStatus(member, query));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@Parameter(hidden = true) @Login Member member) {
        ProfileResponse profileResponse = profileService.getProfile(member.getId());
        return ResponseEntity.ok(profileResponse);
    }

    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long memberId) {
        ProfileResponse profileResponse = profileService.getProfile(memberId);
        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/me/profile/name")
    public ResponseEntity<ProfileResponse> updateName(
            @Parameter(hidden = true) @Login Member member, @Validated @RequestBody ChangeProfileNameRequest request) {
        ProfileResponse profileResponse = profileService.updateName(member.getId(), request.name());

        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/me/profile/description")
    public ResponseEntity<ProfileResponse> updateDescription(
            @Parameter(hidden = true) @Login Member member,
            @Validated @RequestBody ChangeProfileDescriptionRequest request) {
        ProfileResponse profileResponse = profileService.updateDescription(member.getId(), request.description());

        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/me/profile/image-url")
    public ResponseEntity<ProfileResponse> updateImageUrl(
            @Parameter(hidden = true) @Login Member member,
            @Validated @RequestBody ChangeProfileImageUrlRequest request) {
        ProfileResponse profileResponse = profileService.updateImageUrl(member.getId(), request.imageUrl());

        return ResponseEntity.ok(profileResponse);
    }
}
