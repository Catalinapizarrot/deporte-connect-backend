package com.deporteconnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * InscripciÃ³n de un usuario a una actividad.
 * Relaciona User â†” Activity con metadata (fecha de inscripciÃ³n, rol).
 */
@Entity
@Table(
    name = "participaciones",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "activity_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Role role = Role.PARTICIPANTE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Status status = Status.INSCRITO;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

    public enum Role {
        ORGANIZADOR, PARTICIPANTE
    }

    public enum Status {
        INSCRITO, CANCELADO
    }
}
