package com.deporteconnect.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * CatÃ¡logo de deportes disponibles.
 * Se carga al iniciar desde data.sql (FÃºtbol, PÃ¡del, BÃ¡squetbol, etc.)
 */
@Entity
@Table(name = "deportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** Ãcono de referencia (ej: "ic_soccer") */
    @Column(length = 50)
    private String iconKey;

    /** Color hex asociado al deporte */
    @Column(length = 10)
    private String color;
}
