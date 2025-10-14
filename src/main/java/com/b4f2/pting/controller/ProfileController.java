package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.ChangeProfileDescriptionRequest;
import com.b4f2.pting.dto.ChangeProfileImageUrlRequest;
import com.b4f2.pting.dto.ChangeProfileNameRequest;
import com.b4f2.pting.dto.ProfileResponse;
import com.b4f2.pting.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/members")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@Login Member member) {
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
            @Login Member member, @Validated @RequestBody ChangeProfileNameRequest request) {
        ProfileResponse profileResponse = profileService.updateName(member.getId(), request.name());

        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/me/profile/description")
    public ResponseEntity<ProfileResponse> updateDescription(
            @Login Member member, @Validated @RequestBody ChangeProfileDescriptionRequest request) {
        ProfileResponse profileResponse = profileService.updateDescription(member.getId(), request.description());

        return ResponseEntity.ok(profileResponse);
    }

    @PatchMapping("/me/profile/image-url")
    public ResponseEntity<ProfileResponse> updateImageUrl(
            @Login Member member, @Validated @RequestBody ChangeProfileImageUrlRequest request) {
        ProfileResponse profileResponse = profileService.updateImageUrl(member.getId(), request.imageUrl());

        return ResponseEntity.ok(profileResponse);
    }
}
