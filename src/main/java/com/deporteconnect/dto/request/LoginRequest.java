package com.deporteconnect.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no vÃ¡lido")
    private String email;

    @NotBlank(message = "La contraseÃ±a es obligatoria")
    private String password;
}
