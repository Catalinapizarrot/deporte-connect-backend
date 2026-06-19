package com.deporteconnect.repository;

import com.deporteconnect.model.Activity;
import com.deporteconnect.model.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByStatusAndEventAtAfter(ActivityStatus status, LocalDateTime now);

    /** Actividades en las que un usuario participa (organizador o jugador) */
    @Query("""
        SELECT DISTINCT a FROM Activity a
        JOIN a.participations p
        WHERE p.user.id = :userId
        ORDER BY a.eventAt ASC
        """)
    List<Activity> findMyActivities(@Param("userId") Long userId);
}
