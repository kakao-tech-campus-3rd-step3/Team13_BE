package com.b4f2.pting.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.SchoolRequest;
import com.b4f2.pting.repository.SchoolRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final MemberService memberService;

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School getSchoolById(Long schoolId) {
        return schoolRepository.findById(schoolId)
            .orElseThrow(() -> new EntityNotFoundException("학교 정보가 존재하지 않습니다."));
    }

    @Transactional
    public School createSchool(SchoolRequest request) {
        School school = new School(request.name(), request.domain());
        return schoolRepository.save(school);
    }

    @Transactional
    public School selectSchool(Member member, Long schoolId) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new EntityNotFoundException("학교 정보가 존재하지 않습니다."));
        memberService.updateSchool(member, school);
        return school;
    }
}
