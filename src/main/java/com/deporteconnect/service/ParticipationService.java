package com.deporteconnect.service;

import com.deporteconnect.dto.response.ActivityResponse;
import com.deporteconnect.exception.BusinessException;
import com.deporteconnect.exception.ResourceNotFoundException;
import com.deporteconnect.model.Activity;
import com.deporteconnect.model.ActivityStatus;
import com.deporteconnect.model.Participation;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.ActivityRepository;
import com.deporteconnect.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final ActivityRepository activityRepository;

    @Transactional
    public ActivityResponse joinActivity(User user, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        if (activity.getStatus() != ActivityStatus.OPEN) {
            throw new BusinessException("La actividad no estÃ¡ disponible");
        }

        if (activity.getEventAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("La actividad ya finalizÃ³");
        }

        // Ver si ya existe una participaciÃ³n (activa o cancelada previamente)
        var existing = participationRepository.findByUserAndActivity(user, activity);
        if (existing.isPresent()) {
            Participation p = existing.get();
            if (p.getStatus() == Participation.Status.INSCRITO) {
                throw new BusinessException("Ya estÃ¡s inscrito en esta actividad");
            }
            // Reactivar inscripciÃ³n previa cancelada
            p.setStatus(Participation.Status.INSCRITO);
            participationRepository.save(p);
        } else {
            // Validar cupos disponibles
            long current = participationRepository.countByActivityAndStatus(
                    activity, Participation.Status.INSCRITO
            );
            if (current >= activity.getMaxParticipants()) {
                throw new BusinessException("La actividad ya estÃ¡ llena");
            }

            Participation p = Participation.builder()
                    .user(user)
                    .activity(activity)
                    .role(Participation.Role.PARTICIPANTE)
                    .status(Participation.Status.INSCRITO)
                    .joinedAt(LocalDateTime.now())
                    .build();
            participationRepository.save(p);
        }

        // Recargar para reflejar la inscripciÃ³n nueva
        activity = activityRepository.findById(activityId).orElseThrow();
        return ActivityResponse.from(activity);
    }

    @Transactional
    public void leaveActivity(User user, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        Participation p = participationRepository.findByUserAndActivity(user, activity)
                .orElseThrow(() -> new BusinessException("No estÃ¡s inscrito en esta actividad"));

        if (p.getRole() == Participation.Role.ORGANIZADOR) {
            throw new BusinessException(
                    "El organizador no puede salir. Debe cancelar la actividad."
            );
        }

        p.setStatus(Participation.Status.CANCELADO);
        participationRepository.save(p);
    }
}