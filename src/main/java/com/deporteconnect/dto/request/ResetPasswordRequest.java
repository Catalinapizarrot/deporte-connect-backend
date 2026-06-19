package com.deporteconnect.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, message = "MÃ­nimo 6 caracteres")
    private String newPassword;
}
