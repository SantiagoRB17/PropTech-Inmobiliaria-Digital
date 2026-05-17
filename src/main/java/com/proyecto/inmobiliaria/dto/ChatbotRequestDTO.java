package com.proyecto.inmobiliaria.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada al chatbot.
 * El campo message no puede ser vacío (validado con @NotBlank).
 */
public record ChatbotRequestDTO(@NotBlank String message) {}
