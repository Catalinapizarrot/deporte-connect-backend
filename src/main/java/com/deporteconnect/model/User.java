package com.deporteconnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 120)
    private String fullName;

    @Column(length = 30)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Gender gender;

    /**
     * Avatar del usuario. Puede contener:
     *   - "color:#XXXXXX" â†’ avatar de color sÃ³lido
     *   - base64 de una foto comprimida
     *   - null â†’ sin avatar
     *
     * Se usa columnDefinition=TEXT para permitir cadenas largas (las fotos
     * base64 superan fÃ¡cilmente 500 caracteres).
     */
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private SkillLevel skillLevel = SkillLevel.PRINCIPIANTE;

    @Column(name = "profile_complete", nullable = false)
    @Builder.Default
    private Boolean profileComplete = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_intereses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "sport_id")
    )
    @Builder.Default
    private Set<Sport> interests = new HashSet<>();

    // â­ NUEVO: rating de participante (precargado para la demo)
    @Column(name = "participant_rating", precision = 2, scale = 1)
    private BigDecimal participantRating;

    @Column(name = "participant_rating_count")
    @Builder.Default
    private Integer participantRatingCount = 0;

    @Column(name = "organizer_rating", precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal organizerRating = BigDecimal.ZERO;

    @Column(name = "organizer_rating_count")
    @Builder.Default
    private Integer organizerRatingCount = 0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
