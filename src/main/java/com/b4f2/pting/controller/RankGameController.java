package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.dto.RankGameConfirmRequest;
import com.b4f2.pting.dto.RankGameEnqueueRequest;
import com.b4f2.pting.service.MatchingService;
import com.b4f2.pting.service.RankGameService;

@RestController
@RequestMapping("/api/v1/rank-games")
@RequiredArgsConstructor
public class RankGameController {

    private final MatchingService matchingService;
    private final RankGameService rankGameService;

    // 랭크 매칭 대기열 등록 (사용자가 '참가하기' 버튼 클릭 시)
    @PostMapping("/enqueue")
    public ResponseEntity<Void> enqueue(@Login Member member, @RequestBody RankGameEnqueueRequest request) {
        matchingService.addPlayerToQueue(member, request);
        return ResponseEntity.ok().build();
    }

    // 매칭 참가 의사 등록 (사용자가 '참가' or '거절' 버튼 클릭 시)
    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@Login Member member, @RequestBody RankGameConfirmRequest request) {
        matchingService.acceptTeam(member, request);
        return ResponseEntity.ok().build();
    }
}
