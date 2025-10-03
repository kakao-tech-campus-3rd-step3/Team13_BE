package com.b4f2.pting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.b4f2.pting.domain.GameReport;
import com.b4f2.pting.domain.ReportStatus;

@Repository
public interface GameReportRepository extends JpaRepository<GameReport, Long> {

    List<GameReport> findByGameId(Long gameId);

    List<GameReport> findByReporterId(Long reporterId);

    List<GameReport> findByReportedId(Long reportedId);

    List<GameReport> findByStatus(ReportStatus status);
}
