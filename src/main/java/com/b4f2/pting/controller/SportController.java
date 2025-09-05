package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.dto.SportsResponse;
import com.b4f2.pting.service.SportService;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public ResponseEntity<SportsResponse> getAllSports() {
        return ResponseEntity.ok(sportService.findAllSports());
    }
}
