package com.deporteconnect.controller;

import com.deporteconnect.dto.request.CreateLocationRequest;
import com.deporteconnect.dto.response.LocationResponse;
import com.deporteconnect.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubicaciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "6. Ubicaciones", description = "Canchas, centros deportivos, parques")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @Operation(summary = "Listar todas las ubicaciones")
    public ResponseEntity<List<LocationResponse>> getAll() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva ubicaciÃ³n")
    public ResponseEntity<LocationResponse> create(@Valid @RequestBody CreateLocationRequest request) {
        return ResponseEntity.ok(locationService.create(request));
    }
}
