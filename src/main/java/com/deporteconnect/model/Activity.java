package com.deporteconnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actividades")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(name = "event_at", nullable = false)
    private LocalDateTime eventAt;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    /** Precio opcional. Default 0 = gratis. */
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private SkillLevel level = SkillLevel.PRINCIPIANTE;

    /** RestricciÃ³n de gÃ©nero de la actividad. Default MIXTO. */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ActivityGender gender = ActivityGender.MIXTO;

    @Column(name = "requires_reservation")
    @Builder.Default
    private Boolean requiresReservation = false;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ActivityStatus status = ActivityStatus.OPEN;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Participation> participations = new HashSet<>();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Contador rÃ¡pido de participantes activos */
    public int getCurrentParticipants() {
        return participations == null ? 0 : participations.size();
    }
}
