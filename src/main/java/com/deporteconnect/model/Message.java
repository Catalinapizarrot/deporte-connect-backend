package com.deporteconnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Mensaje en el chat de una actividad.
 * Cada actividad tiene su propio hilo de mensajes.
 */
@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private MessageType type = MessageType.USER;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
    }

    /**
     * Tipo de mensaje:
     *  - USER: mensaje regular de un usuario
     *  - SYSTEM: anuncios del sistema (ej: "Uniformes amarillos")
     */
    public enum MessageType {
        USER, SYSTEM
    }
}
