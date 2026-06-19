package com.deporteconnect.controller;

import com.deporteconnect.dto.request.CreateActivityRequest;
import com.deporteconnect.dto.response.ActivityResponse;
import com.deporteconnect.model.User;
import com.deporteconnect.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividades")
@RequiredArgsConstructor
@Tag(name = "3. Actividades", description = "Crear, listar y participar en actividades")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Feed de actividades disponibles")
    public ResponseEntity<List<ActivityResponse>> getFeed(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(activityService.getFeed(currentUser));
    }

    @GetMapping("/mis-partidos")
    @Operation(summary = "Listar actividades en las que participo")
    public ResponseEntity<List<ActivityResponse>> getMyMatches(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(activityService.getMyMatches(currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una actividad")
    public ResponseEntity<ActivityResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva actividad")
    public ResponseEntity<ActivityResponse> create(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateActivityRequest request
    ) {
        return ResponseEntity.ok(activityService.create(currentUser, request));
    }

    @PostMapping("/{id}/unirse")
    @Operation(summary = "Unirme a una actividad")
    public ResponseEntity<ActivityResponse> join(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(activityService.joinActivity(currentUser, id));
    }

    @DeleteMapping("/{id}/salirse")
    @Operation(summary = "Salirme de una actividad")
    public ResponseEntity<Void> leave(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        activityService.leaveActivity(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar actividad (solo organizador)")
    public ResponseEntity<Void> cancel(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        activityService.cancelActivity(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}
