package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.dto.ChatbotContextDTO;
import com.proyecto.inmobiliaria.dto.ChatbotRequestDTO;
import com.proyecto.inmobiliaria.dto.ChatbotResponseDTO;
import com.proyecto.inmobiliaria.service.ChatbotContextService;
import com.proyecto.inmobiliaria.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints del chatbot para asesores.
 * POST /api/v1/chatbot/message  → reenvía el mensaje al webhook n8n y devuelve la respuesta.
 * GET  /api/v1/chatbot/context  → devuelve el snapshot del sistema (n8n lo consulta antes de responder).
 */
@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ChatbotContextService chatbotContextService;

    @PostMapping("/message")
    public ResponseEntity<ChatbotResponseDTO> sendMessage(@Valid @RequestBody ChatbotRequestDTO request) {
        return ResponseEntity.ok(chatbotService.sendMessage(request.message()));
    }

    @GetMapping("/context")
    public ResponseEntity<ChatbotContextDTO> getContext() {
        return ResponseEntity.ok(chatbotContextService.buildContext());
    }
}
