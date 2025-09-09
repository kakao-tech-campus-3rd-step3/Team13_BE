package com.b4f2.pting.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.domain.Member;
import com.b4f2.pting.domain.School;
import com.b4f2.pting.dto.SchoolRequest;
import com.b4f2.pting.dto.SchoolResponse;
import com.b4f2.pting.dto.SchoolsResponse;
import com.b4f2.pting.repository.SchoolRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final MemberService memberService;

    public SchoolsResponse getAllSchools() {
        List<SchoolResponse> schoolResponseList = schoolRepository.findAll()
            .stream()
            .map(school -> new SchoolResponse(school.getId(), school.getName(), school.getDomain()))
            .toList();
        return new SchoolsResponse(schoolResponseList);
    }

    public SchoolResponse getSchoolById(Long schoolId) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new EntityNotFoundException("학교 정보가 존재하지 않습니다."));

        return new SchoolResponse(school.getId(), school.getName(), school.getDomain());
    }

    @Transactional
    public SchoolResponse createSchool(SchoolRequest request) {
        School school = new School(request.name(), request.domain());
        School savedSchool = schoolRepository.save(school);
        return new SchoolResponse(savedSchool.getId(), savedSchool.getName(), savedSchool.getDomain());
    }

    @Transactional
    public SchoolResponse selectSchool(Member member, Long schoolId) {
        School school = schoolRepository.findById(schoolId)
            .orElseThrow(() -> new EntityNotFoundException("학교 정보가 존재하지 않습니다."));
        memberService.updateSchool(member, school);
        return new SchoolResponse(school.getId(), school.getName(), school.getDomain());
    }
}
