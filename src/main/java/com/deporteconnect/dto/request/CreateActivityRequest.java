package com.deporteconnect.dto.request;

import com.deporteconnect.model.ActivityGender;
import com.deporteconnect.model.SkillLevel;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateActivityRequest {

    @NotBlank(message = "El tÃ­tulo es obligatorio")
    @Size(min = 3, max = 150)
    private String title;

    @NotNull(message = "Selecciona un deporte")
    private Long sportId;

    @NotNull(message = "Selecciona una ubicaciÃ³n")
    private Long locationId;

    @NotNull(message = "La fecha y hora del evento son obligatorias")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime eventAt;

    @NotNull(message = "Indica el cupo mÃ¡ximo")
    @Min(value = 2, message = "MÃ­nimo 2 participantes")
    @Max(value = 100, message = "MÃ¡ximo 100 participantes")
    private Integer maxParticipants;

    /** Opcional. Si no se envÃ­a, queda en 0 (gratis). */
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal price;

    @NotNull(message = "Selecciona el nivel")
    private SkillLevel level;

    /** RestricciÃ³n de gÃ©nero de la actividad (HOMBRES / MIXTO / MUJERES) */
    @NotNull(message = "Indica el pÃºblico")
    private ActivityGender gender;

    private Boolean requiresReservation;

    @Size(max = 1000)
    private String description;
}
