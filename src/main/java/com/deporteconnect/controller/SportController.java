package com.deporteconnect.controller;

import com.deporteconnect.dto.response.SportResponse;
import com.deporteconnect.service.SportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deportes")
@RequiredArgsConstructor
@Tag(name = "5. Deportes", description = "CatÃ¡logo de deportes (pÃºblico)")
public class SportController {

    private final SportService sportService;

    @GetMapping
    @Operation(summary = "Listar todos los deportes disponibles")
    public ResponseEntity<List<SportResponse>> getAll() {
        return ResponseEntity.ok(sportService.getAll());
    }
}
