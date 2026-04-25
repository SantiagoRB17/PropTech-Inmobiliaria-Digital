package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Visita;
import com.proyecto.inmobiliaria.model.enums.EstadoVisita;
import com.proyecto.inmobiliaria.service.VisitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de visitas a inmuebles.
 *
 * Flujo de una visita:
 *   1. POST /visitas                    → solicitar visita (ingresa a la ColaAtencion FIFO)
 *   2. POST /visitas/atender-siguiente  → desencola de la ColaAtencion y pasa a ColaPrioridad
 *   3. POST /visitas/despachar-prioritaria → extrae visita de mayor prioridad del heap
 */
@RestController
@RequestMapping("/visitas")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;

    /**
     * POST /visitas
     * Solicita una visita. Calcula la prioridad del cliente y encola en la ColaAtencion (FIFO).
     */
    @PostMapping
    public ResponseEntity<Visita> solicitarVisita(@RequestBody Visita visita) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitaService.solicitarVisita(visita));
    }

    /** GET /visitas — Lista todas las visitas registradas. */
    @GetMapping
    public ResponseEntity<List<Visita>> listarTodas() {
        return ResponseEntity.ok(visitaService.listarTodas());
    }

    /** GET /visitas/{id} — Busca una visita por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Visita> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(visitaService.buscarPorId(id));
    }

    /**
     * GET /visitas/prioritarias
     * Lista las visitas actuales en la cola de prioridad (Max-Heap), sin extraerlas.
     */
    @GetMapping("/prioritarias")
    public ResponseEntity<List<Visita>> listarVisitasPrioritarias() {
        return ResponseEntity.ok(visitaService.listarVisitasPrioritarias());
    }

    /**
     * POST /visitas/atender-siguiente
     * Atiende la siguiente solicitud de la ColaAtencion (FIFO) y la mueve a la ColaPrioridad.
     */
    @PostMapping("/atender-siguiente")
    public ResponseEntity<Visita> atenderSiguienteSolicitud() {
        return ResponseEntity.ok(visitaService.atenderSiguienteSolicitud());
    }

    /**
     * POST /visitas/despachar-prioritaria
     * Extrae la visita con mayor prioridad del heap y la marca como CONFIRMADA.
     */
    @PostMapping("/despachar-prioritaria")
    public ResponseEntity<Visita> despacharVisitaPrioritaria() {
        return ResponseEntity.ok(visitaService.despacharVisitaPrioritaria());
    }

    /**
     * PATCH /visitas/{id}/estado
     * Actualiza el estado de una visita manualmente.
     * Body: { "estado": "REALIZADA" }
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Visita> actualizarEstado(@PathVariable String id,
                                                    @RequestBody Map<String, String> body) {
        EstadoVisita nuevoEstado = EstadoVisita.valueOf(body.get("estado"));
        return ResponseEntity.ok(visitaService.actualizarEstado(id, nuevoEstado));
    }
}
