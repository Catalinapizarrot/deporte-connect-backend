package com.deporteconnect.repository;

import com.deporteconnect.model.Activity;
import com.deporteconnect.model.Participation;
import com.deporteconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // MÃ©todos por ID (usados en ActivityService)
    boolean existsByUserIdAndActivityId(Long userId, Long activityId);

    Optional<Participation> findByUserIdAndActivityId(Long userId, Long activityId);

    // MÃ©todos por entidad (usados en MessageService y ParticipationService)
    boolean existsByUserAndActivity(User user, Activity activity);

    Optional<Participation> findByUserAndActivity(User user, Activity activity);

    // Contador para validar cupos
    long countByActivityAndStatus(Activity activity, Participation.Status status);
}