package com.deporteconnect.controller;

import com.deporteconnect.dto.request.UpdateProfileRequest;
import com.deporteconnect.dto.response.UserResponse;
import com.deporteconnect.model.User;
import com.deporteconnect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "2. Usuarios", description = "Perfil del usuario autenticado")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Mi perfil")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMe(currentUser));
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(currentUser, request));
    }

    @PutMapping("/me/avatar")
    @Operation(summary = "Actualizar mi foto de perfil")
    public ResponseEntity<UserResponse> updateAvatar(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> body
    ) {
        String avatarUrl = body.get("avatarUrl");
        return ResponseEntity.ok(userService.updateAvatar(currentUser, avatarUrl));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil pÃºblico de otro usuario")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
}
