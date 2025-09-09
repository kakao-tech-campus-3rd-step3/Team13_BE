package com.b4f2.pting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.b4f2.pting.domain.School;

public interface SchoolRepository extends JpaRepository<School, Long> {

}
