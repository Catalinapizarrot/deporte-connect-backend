package com.deporteconnect.service;

import com.deporteconnect.dto.request.CreateActivityRequest;
import com.deporteconnect.dto.request.CreateActivityReportRequest;
import com.deporteconnect.dto.request.CreateOrganizerRatingRequest;
import com.deporteconnect.dto.response.ActivityResponse;
import com.deporteconnect.exception.BusinessException;
import com.deporteconnect.exception.ResourceNotFoundException;
import com.deporteconnect.model.Activity;
import com.deporteconnect.model.ActivityGender;
import com.deporteconnect.model.ActivityReport;
import com.deporteconnect.model.ActivityStatus;
import com.deporteconnect.model.Gender;
import com.deporteconnect.model.Location;
import com.deporteconnect.model.OrganizerRating;
import com.deporteconnect.model.Participation;
import com.deporteconnect.model.Sport;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.ActivityReportRepository;
import com.deporteconnect.repository.ActivityRepository;
import com.deporteconnect.repository.LocationRepository;
import com.deporteconnect.repository.OrganizerRatingRepository;
import com.deporteconnect.repository.ParticipationRepository;
import com.deporteconnect.repository.SportRepository;
import com.deporteconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private static final long REPORTS_UNDER_REVIEW_THRESHOLD = 3;

    private final ActivityRepository activityRepository;
    private final ActivityReportRepository activityReportRepository;
    private final SportRepository sportRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRepository participationRepository;
    private final OrganizerRatingRepository organizerRatingRepository;
    private final UserRepository userRepository;

    /** Feed de actividades disponibles (no canceladas, futuras), filtrado por gÃ©nero */
    @Transactional(readOnly = true)
    public List<ActivityResponse> getFeed(User currentUser) {
        List<Activity> activities = activityRepository.findByStatusAndEventAtAfter(
                ActivityStatus.OPEN, LocalDateTime.now()
        );

        Gender userGender = currentUser.getGender();
        return activities.stream()
                .filter(a -> isGenderCompatible(a.getGender(), userGender))
                .map(this::loadAndConvert)
                .collect(Collectors.toList());
    }

    /** Mis partidos (organizados o donde participo) */
    @Transactional(readOnly = true)
    public List<ActivityResponse> getMyMatches(User currentUser) {
        List<Activity> activities = activityRepository.findMyActivities(currentUser.getId());
        return activities.stream()
                .map(this::loadAndConvert)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActivityResponse getById(Long id) {
        Activity a = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada: " + id));
        return loadAndConvert(a);
    }

    @Transactional
    public ActivityResponse create(User currentUser, CreateActivityRequest request) {
        if (!Boolean.TRUE.equals(currentUser.getProfileComplete())) {
            throw new BusinessException("Debes completar tu perfil antes de crear actividades");
        }
        if (currentUser.getPhone() == null || currentUser.getPhone().isBlank()) {
            throw new BusinessException("Debes registrar un telefono antes de crear actividades");
        }
        if (currentUser.getBirthDate() == null) {
            throw new BusinessException("Debes registrar tu fecha de nacimiento antes de crear actividades");
        }
        if (Period.between(currentUser.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new BusinessException("Debes ser mayor de 18 anos para crear actividades");
        }
        if (currentUser.getGender() == null) {
            throw new BusinessException("Debes completar tu perfil con tu gÃ©nero antes de crear actividades");
        }

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new ResourceNotFoundException("Deporte no encontrado"));
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("UbicaciÃ³n no encontrada"));

        Activity activity = Activity.builder()
                .title(request.getTitle().trim())
                .sport(sport)
                .location(location)
                .organizer(currentUser)
                .eventAt(request.getEventAt())
                .maxParticipants(request.getMaxParticipants())
                .price(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice())
                .level(request.getLevel())
                .gender(request.getGender())
                .requiresReservation(Boolean.TRUE.equals(request.getRequiresReservation()))
                .description(request.getDescription())
                .status(ActivityStatus.OPEN)
                .build();

        activity = activityRepository.save(activity);

        // El organizador queda inscrito automÃ¡ticamente
        Participation p = Participation.builder()
                .user(currentUser)
                .activity(activity)
                .role(Participation.Role.ORGANIZADOR)
                .status(Participation.Status.INSCRITO)
                .joinedAt(LocalDateTime.now())
                .build();
        participationRepository.save(p);

        return loadAndConvert(activityRepository.findById(activity.getId()).orElseThrow());
    }

    @Transactional
    public ActivityResponse joinActivity(User currentUser, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        if (activity.getStatus() != ActivityStatus.OPEN) {
            throw new BusinessException("Esta actividad ya no acepta participantes");
        }
        if (activity.getEventAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Esta actividad ya finalizÃ³");
        }
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new BusinessException("Esta actividad estÃ¡ llena");
        }

        if (!isGenderCompatible(activity.getGender(), currentUser.getGender())) {
            throw new BusinessException("Esta actividad no estÃ¡ disponible para tu gÃ©nero");
        }

        if (participationRepository.existsByUserIdAndActivityId(currentUser.getId(), activityId)) {
            throw new BusinessException("Ya estÃ¡s inscrito en esta actividad");
        }

        Participation p = Participation.builder()
                .user(currentUser)
                .activity(activity)
                .role(Participation.Role.PARTICIPANTE)
                .status(Participation.Status.INSCRITO)
                .joinedAt(LocalDateTime.now())
                .build();
        participationRepository.save(p);

        return loadAndConvert(activityRepository.findById(activityId).orElseThrow());
    }

    @Transactional
    public void leaveActivity(User currentUser, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        Participation p = participationRepository
                .findByUserIdAndActivityId(currentUser.getId(), activityId)
                .orElseThrow(() -> new BusinessException("No estÃ¡s inscrito en esta actividad"));

        if (p.getRole() == Participation.Role.ORGANIZADOR) {
            throw new BusinessException("Eres el organizador. Cancela la actividad si no quieres participar.");
        }

        participationRepository.delete(p);
    }

    @Transactional
    public void cancelActivity(User currentUser, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        if (!activity.getOrganizer().getId().equals(currentUser.getId())) {
            throw new BusinessException("Solo el organizador puede cancelar la actividad");
        }

        activity.setStatus(ActivityStatus.CANCELLED);
        activityRepository.save(activity);
    }

    @Transactional
    public ActivityResponse finishActivity(User currentUser, Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        if (!activity.getOrganizer().getId().equals(currentUser.getId())) {
            throw new BusinessException("Solo el organizador puede finalizar la actividad");
        }
        if (activity.getStatus() != ActivityStatus.OPEN) {
            throw new BusinessException("Solo se pueden finalizar actividades abiertas");
        }
        if (activity.getEventAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Solo se puede finalizar una actividad cuando su fecha ya paso");
        }

        activity.setStatus(ActivityStatus.FINISHED);
        activity = activityRepository.save(activity);
        return loadAndConvert(activity);
    }

    @Transactional
    public ActivityResponse rateOrganizer(User currentUser, Long activityId, CreateOrganizerRatingRequest request) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        User organizer = activity.getOrganizer();
        if (activity.getStatus() != ActivityStatus.FINISHED) {
            throw new BusinessException("Solo puedes calificar actividades finalizadas");
        }
        if (organizer.getId().equals(currentUser.getId())) {
            throw new BusinessException("No puedes calificarte a ti mismo como organizador");
        }

        Participation participation = participationRepository
                .findByUserIdAndActivityId(currentUser.getId(), activityId)
                .orElseThrow(() -> new BusinessException("Solo los participantes pueden calificar al organizador"));

        if (participation.getRole() != Participation.Role.PARTICIPANTE) {
            throw new BusinessException("Solo los participantes pueden calificar al organizador");
        }
        if (participation.getStatus() != Participation.Status.INSCRITO) {
            throw new BusinessException("Solo participantes inscritos pueden calificar al organizador");
        }
        if (organizerRatingRepository.existsByActivityIdAndRaterId(activityId, currentUser.getId())) {
            throw new BusinessException("Ya calificaste al organizador de esta actividad");
        }

        OrganizerRating rating = OrganizerRating.builder()
                .activity(activity)
                .organizer(organizer)
                .rater(currentUser)
                .stars(request.getStars())
                .createdAt(LocalDateTime.now())
                .build();
        organizerRatingRepository.save(rating);

        BigDecimal currentAverage = organizer.getOrganizerRating() == null
                ? BigDecimal.ZERO
                : organizer.getOrganizerRating();
        int currentCount = organizer.getOrganizerRatingCount() == null
                ? 0
                : organizer.getOrganizerRatingCount();
        int newCount = currentCount + 1;
        BigDecimal newAverage = currentAverage
                .multiply(BigDecimal.valueOf(currentCount))
                .add(BigDecimal.valueOf(request.getStars()))
                .divide(BigDecimal.valueOf(newCount), 1, RoundingMode.HALF_UP);

        organizer.setOrganizerRating(newAverage);
        organizer.setOrganizerRatingCount(newCount);
        userRepository.save(organizer);

        return loadAndConvert(activity);
    }

    @Transactional
    public void reportActivity(User currentUser, Long activityId, CreateActivityReportRequest request) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException("Debes iniciar sesion para reportar una actividad");
        }
        if (!Boolean.TRUE.equals(currentUser.getProfileComplete())) {
            throw new BusinessException("Debes completar tu perfil antes de reportar actividades");
        }

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));

        if (activityReportRepository.existsByReporterIdAndActivityId(currentUser.getId(), activityId)) {
            throw new BusinessException("Ya reportaste esta actividad");
        }

        ActivityReport report = ActivityReport.builder()
                .activity(activity)
                .reporter(currentUser)
                .reason(request.getReason().trim())
                .description(request.getDescription() == null ? null : request.getDescription().trim())
                .createdAt(LocalDateTime.now())
                .build();

        activityReportRepository.save(report);

        long reportCount = activityReportRepository.countDistinctByActivityId(activityId);
        if (reportCount >= REPORTS_UNDER_REVIEW_THRESHOLD && activity.getStatus() == ActivityStatus.OPEN) {
            activity.setStatus(ActivityStatus.UNDER_REVIEW);
            activityRepository.save(activity);
        }
    }

    // â”€â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Filtro ESTRICTO de gÃ©nero:
     *   MIXTO   â†’ cualquiera puede unirse
     *   HOMBRES â†’ solo HOMBRE
     *   MUJERES â†’ solo MUJER
     */
    private boolean isGenderCompatible(ActivityGender activityGender, Gender userGender) {
        if (activityGender == null || activityGender == ActivityGender.MIXTO) return true;
        if (userGender == null) return false;
        if (activityGender == ActivityGender.HOMBRES && userGender == Gender.HOMBRE) return true;
        if (activityGender == ActivityGender.MUJERES && userGender == Gender.MUJER) return true;
        return false;
    }

    private ActivityResponse loadAndConvert(Activity a) {
        a.getSport().getName();
        a.getLocation().getName();
        a.getOrganizer().getFullName();
        a.getCurrentParticipants();
        return ActivityResponse.from(a);
    }
}
