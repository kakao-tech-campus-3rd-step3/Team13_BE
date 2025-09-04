package com.b4f2.pting.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.b4f2.pting.dto.SportResponse;
import com.b4f2.pting.dto.SportsResponse;
import com.b4f2.pting.repository.SportRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SportService {

    private final SportRepository sportRepository;

    public SportsResponse findAllSports() {
        List<SportResponse> sportResponseList = sportRepository.findAll()
            .stream()
            .map(SportResponse::new)
            .toList();

        return new SportsResponse(sportResponseList);
    }
}
