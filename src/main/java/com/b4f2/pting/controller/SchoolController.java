package com.b4f2.pting.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.SchoolRequest;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.dto.SchoolsResponse;
import com.b4f2.pting.service.SchoolService;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
@Hidden
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<SchoolsResponse> getSchools() {
        List<SchoolResponse> responseList = schoolService.getAllSchools().stream()
                .map(s -> new SchoolResponse(s.getId(), s.getName(), s.getPostfix()))
                .toList();

        SchoolsResponse responses = new SchoolsResponse(responseList);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<SchoolResponse> getSchoolById(@PathVariable Long schoolId) {
        School school = schoolService.getSchoolById(schoolId);
        SchoolResponse response = new SchoolResponse(school.getId(), school.getName(), school.getPostfix());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SchoolResponse> createSchool(@RequestBody SchoolRequest request) {
        School school = schoolService.createSchool(request);
        SchoolResponse response = new SchoolResponse(school.getId(), school.getName(), school.getPostfix());
        return ResponseEntity.ok(response);
    }
}
