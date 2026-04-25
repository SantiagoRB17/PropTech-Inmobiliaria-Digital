package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Alerta;
import com.proyecto.inmobiliaria.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para consulta y gestión de alertas del sistema.
 * Las alertas son generadas automáticamente por OperacionService y VisitaService.
 */
@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    /**
     * GET /alertas
     * Lista las alertas activas (no resueltas). Estas requieren atención.
     */
    @GetMapping
    public ResponseEntity<List<Alerta>> listarNoResueltas() {
        return ResponseEntity.ok(alertaService.listarNoResueltas());
    }

    /**
     * GET /alertas/historial
     * Lista todas las alertas generadas en orden cronológico.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<Alerta>> listarHistorial() {
        return ResponseEntity.ok(alertaService.listarHistorial());
    }

    /** GET /alertas/{id} — Busca una alerta por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Alerta> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(alertaService.buscarPorId(id));
    }

    /**
     * PATCH /alertas/{id}/resolver
     * Marca la alerta como resuelta. Sale del listado de activas.
     */
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<Void> marcarResuelta(@PathVariable String id) {
        alertaService.marcarResuelta(id);
        return ResponseEntity.ok().build();
    }
}
