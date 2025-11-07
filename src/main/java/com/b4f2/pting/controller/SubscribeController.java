package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.config.Login;
import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.SubscribeResponse;
import com.b4f2.pting.domain.TimePeriod;
import com.b4f2.pting.dto.SportsResponse;
import com.b4f2.pting.dto.TimePeriodsResponse;
import com.b4f2.pting.service.SubscribeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/me")
@Tag(name = "알림 구독 관리 API")
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping("/interests/times/{timePeriod}")
    public ResponseEntity<Void> addTimePeriod(@Login Member member, @PathVariable TimePeriod timePeriod) {
        subscribeService.addInterestTime(member, timePeriod);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/interests/times/{timePeriod}")
    public ResponseEntity<Void> deleteTimePeriod(@Login Member member, @PathVariable TimePeriod timePeriod) {
        subscribeService.deleteInterestTime(member, timePeriod);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/interests/sports/{sportId}")
    public ResponseEntity<Void> addSport(@Login Member member, @PathVariable Long sportId) {
        subscribeService.addInterestSport(member, sportId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/interests/times/{sportId}")
    public ResponseEntity<Void> deleteSport(@Login Member member, @PathVariable Long sportId) {
        subscribeService.deleteInterestSport(member, sportId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/interests/sports")
    public ResponseEntity<SportsResponse> getSports(@Login Member member) {
        return ResponseEntity.ok(subscribeService.getSportsByMember(member));
    }

    @GetMapping("/interests/times")
    public ResponseEntity<TimePeriodsResponse> getTimes(@Login Member member) {
        return ResponseEntity.ok(subscribeService.getTimesByMember(member));
    }

    @GetMapping("/subscribes")
    public ResponseEntity<SubscribeResponse> getSubscribes(@Login Member member) {
        return ResponseEntity.ok(subscribeService.getSubscribesByMember(member));
    }

    @PostMapping("/subscribes")
    public ResponseEntity<Void> subscribe(@Login Member member) {
        subscribeService.setSubscribed(member, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subscribes")
    public ResponseEntity<SubscribeResponse> unsubscribe(@Login Member member) {
        subscribeService.setSubscribed(member, false);
        return ResponseEntity.ok().build();
    }
}
