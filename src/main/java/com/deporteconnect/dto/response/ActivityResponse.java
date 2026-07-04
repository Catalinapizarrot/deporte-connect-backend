package com.deporteconnect.dto.response;

import com.deporteconnect.model.Activity;
import com.deporteconnect.model.ActivityGender;
import com.deporteconnect.model.ActivityStatus;
import com.deporteconnect.model.SkillLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Data
@Builder
public class ActivityResponse {
    private Long id;
    private String title;
    private SportResponse sport;
    private LocationResponse location;
    private Long organizerId;
    private String organizerName;
    private Boolean organizerVerified;
    private LocalDateTime eventAt;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal price;
    private SkillLevel level;
    private ActivityGender gender;
    private Boolean requiresReservation;
    private String description;
    private ActivityStatus status;

    public static ActivityResponse from(Activity a) {
        return ActivityResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .sport(SportResponse.from(a.getSport()))
                .location(LocationResponse.from(a.getLocation()))
                .organizerId(a.getOrganizer().getId())
                .organizerName(a.getOrganizer().getFullName())
                .organizerVerified(isOrganizerVerified(a))
                .eventAt(a.getEventAt())
                .maxParticipants(a.getMaxParticipants())
                .currentParticipants(a.getCurrentParticipants())
                .price(a.getPrice() == null ? BigDecimal.ZERO : a.getPrice())
                .level(a.getLevel())
                .gender(a.getGender())
                .requiresReservation(a.getRequiresReservation())
                .description(a.getDescription())
                .status(a.getStatus())
                .build();
    }

    private static boolean isOrganizerVerified(Activity a) {
        var organizer = a.getOrganizer();
        if (organizer == null) return false;
        if (!Boolean.TRUE.equals(organizer.getProfileComplete())) return false;
        if (organizer.getPhone() == null || organizer.getPhone().isBlank()) return false;
        if (organizer.getBirthDate() == null) return false;
        if (Period.between(organizer.getBirthDate(), LocalDate.now()).getYears() < 18) return false;
        return organizer.getGender() != null;
    }
}
