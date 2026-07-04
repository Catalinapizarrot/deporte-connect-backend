package com.deporteconnect.repository;

import com.deporteconnect.model.ActivityReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Long> {
    boolean existsByReporterIdAndActivityId(Long reporterId, Long activityId);
}
