package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.dto.ChatbotResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del chatbot: envía el mensaje al webhook de n8n via RestTemplate
 * y extrae el campo "reply" de la respuesta.
 * Flujo: POST /api/v1/chatbot/message
 *   → ChatbotServiceImpl → n8n webhook (localhost:5678/webhook/webhook-chat)
 *   → n8n consulta GET /context, envía a Gemini, devuelve { "reply": "..." }
 */
@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Value("${chatbot.webhook-url}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @SuppressWarnings("unchecked")
    public ChatbotResponseDTO sendMessage(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);

        try {
            Map<String, Object> response = restTemplate.postForObject(webhookUrl, body, Map.class);

            if (response == null || !response.containsKey("reply")) {
                throw new RuntimeException("Respuesta inválida del webhook: falta el campo 'reply'.");
            }

            return new ChatbotResponseDTO((String) response.get("reply"));

        } catch (RestClientException e) {
            throw new RuntimeException(
                    "No se pudo conectar con n8n en " + webhookUrl +
                    ". Verifique que el contenedor esté corriendo y el flujo activo. Detalle: " + e.getMessage()
            );
        }
    }
}
