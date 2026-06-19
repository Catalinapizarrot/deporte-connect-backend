package com.deporteconnect.controller;

import com.deporteconnect.dto.request.*;
import com.deporteconnect.dto.response.AuthResponse;
import com.deporteconnect.exception.ResourceNotFoundException;
import com.deporteconnect.model.User;
import com.deporteconnect.repository.UserRepository;
import com.deporteconnect.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. AutenticaciÃ³n", description = "Registro, login y recuperaciÃ³n de contraseÃ±a")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesiÃ³n")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Verifica si un email existe en el sistema.
     * Por seguridad, devuelve true incluso si no existe (para no permitir
     * enumerar usuarios). En la app siguiente paso es ingresar nueva
     * contraseÃ±a â€” solo se aplica si el email es real.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Verificar email para recuperar contraseÃ±a")
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        boolean exists = userRepository.findByEmail(request.getEmail()).isPresent();
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "exists", exists,
                "message", "Si el email es vÃ¡lido, podrÃ¡s restablecer tu contraseÃ±a"
        ));
    }

    /**
     * Cambia la contraseÃ±a sin pedir la anterior.
     * Es flujo simplificado para el proyecto acadÃ©mico â€” en producciÃ³n
     * usarÃ­amos email + token temporal Ãºnico enviado por correo.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseÃ±a")
    @Transactional
    public ResponseEntity<Map<String, Object>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un usuario con ese email"
                ));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "ContraseÃ±a actualizada correctamente"
        ));
    }
}
