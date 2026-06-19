package com.deporteconnect.controller;

import com.deporteconnect.dto.request.SendMessageRequest;
import com.deporteconnect.dto.response.MessageResponse;
import com.deporteconnect.model.User;
import com.deporteconnect.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actividades/{activityId}/mensajes")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "4. Chat", description = "MensajerÃ­a dentro del chat de cada actividad")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Obtener mensajes del chat",
               description = "Solo los inscritos pueden acceder")
    public ResponseEntity<List<MessageResponse>> getMessages(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long activityId
    ) {
        return ResponseEntity.ok(messageService.getMessages(currentUser, activityId));
    }

    @PostMapping
    @Operation(summary = "Enviar un mensaje")
    public ResponseEntity<MessageResponse> send(
        @AuthenticationPrincipal User currentUser,
        @PathVariable Long activityId,
        @Valid @RequestBody SendMessageRequest request
    ) {
        return ResponseEntity.ok(messageService.sendMessage(currentUser, activityId, request));
    }
}
