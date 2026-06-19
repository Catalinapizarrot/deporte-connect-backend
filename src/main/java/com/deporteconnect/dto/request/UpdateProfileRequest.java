package com.deporteconnect.dto.request;

import com.deporteconnect.model.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String fullName;

    @NotBlank(message = "El telÃ©fono es obligatorio")
    @Size(min = 8, max = 30, message = "TelÃ©fono invÃ¡lido")
    private String phone;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha debe ser pasada")
    private LocalDate birthDate;

    @NotNull(message = "El gÃ©nero es obligatorio")
    private Gender gender;

    private String avatarUrl;
    private String bio;

    @NotEmpty(message = "Selecciona al menos un deporte")
    private Set<Long> interestIds;
}
