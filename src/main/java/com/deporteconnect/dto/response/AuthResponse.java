package com.deporteconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta de los endpoints de auth (login/register).
 * Incluye el token JWT y datos bÃ¡sicos del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private boolean profileComplete;
}
