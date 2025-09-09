package com.b4f2.pting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.dto.SchoolRequest;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.dto.SchoolsResponse;
import com.b4f2.pting.service.SchoolService;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    public ResponseEntity<SchoolsResponse> getSchools() {
        SchoolsResponse response = schoolService.getAllSchools();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<SchoolResponse> getSchoolById(@PathVariable Long schoolId) {
        SchoolResponse response = schoolService.getSchoolById(schoolId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SchoolResponse> createSchool(@RequestBody SchoolRequest request) {
        SchoolResponse response = schoolService.createSchool(request);
        return ResponseEntity.ok(response);
    }


}
