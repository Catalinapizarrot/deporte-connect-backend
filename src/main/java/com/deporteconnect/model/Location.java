package com.deporteconnect.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * UbicaciÃ³n de una actividad deportiva.
 * Incluye lat/lng para georreferenciaciÃ³n.
 */
@Entity
@Table(name = "ubicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 250)
    private String address;

    @Column(length = 80)
    private String commune;

    @Column(length = 80)
    private String city;

    private Double latitude;

    private Double longitude;
}
