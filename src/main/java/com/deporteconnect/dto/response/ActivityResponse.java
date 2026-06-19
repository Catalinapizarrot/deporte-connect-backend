package com.deporteconnect.dto.response;

import com.deporteconnect.model.Activity;
import com.deporteconnect.model.ActivityGender;
import com.deporteconnect.model.ActivityStatus;
import com.deporteconnect.model.SkillLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ActivityResponse {
    private Long id;
    private String title;
    private SportResponse sport;
    private LocationResponse location;
    private Long organizerId;
    private String organizerName;
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
}
