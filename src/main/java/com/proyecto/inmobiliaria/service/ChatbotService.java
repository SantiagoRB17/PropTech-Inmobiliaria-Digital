package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.dto.ChatbotResponseDTO;

/**
 * Contrato del servicio de chatbot.
 * La implementación envía el mensaje al webhook de n8n.
 */
public interface ChatbotService {

    ChatbotResponseDTO sendMessage(String message);
}
