package com.deporteconnect.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no vÃ¡lido")
    private String email;

    @NotBlank(message = "La contraseÃ±a es obligatoria")
    @Size(min = 6, message = "La contraseÃ±a debe tener al menos 6 caracteres")
    private String password;
}
