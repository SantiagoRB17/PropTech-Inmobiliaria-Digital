package com.proyecto.inmobiliaria;

import com.proyecto.inmobiliaria.model.Operacion;
import com.proyecto.inmobiliaria.service.OperacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de operaciones comerciales (ventas y arriendos).
 * Ciclo de vida de una operación:
 *   EN_PROCESO → CERRADA (exitosa) o CANCELADA
 */
@RestController
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;

    /**
     * POST /operaciones
     * Registra una nueva operación. Valida disponibilidad del inmueble
     * y compatibilidad entre tipo de operación y finalidad del inmueble.
     */
    @PostMapping
    public ResponseEntity<Operacion> registrar(@RequestBody Operacion operacion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operacionService.registrar(operacion));
    }

    /** GET /operaciones — Lista las operaciones activas (EN_PROCESO). */
    @GetMapping
    public ResponseEntity<List<Operacion>> listarTodas() {
        return ResponseEntity.ok(operacionService.listarTodas());
    }

    /**
     * GET /operaciones/historial
     * Lista todas las operaciones en orden cronológico (ArrayList de historial).
     */
    @GetMapping("/historial")
    public ResponseEntity<List<Operacion>> listarHistorial() {
        return ResponseEntity.ok(operacionService.listarHistorial());
    }

    /** GET /operaciones/{id} — Busca una operación por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Operacion> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(operacionService.buscarPorId(id));
    }

    /**
     * POST /operaciones/{id}/cerrar
     * Cierra exitosamente la operación:
     *   - Marca el inmueble como no disponible
     *   - Incrementa cierresRealizados del asesor
     *   - Genera alerta de cierre
     */
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<Operacion> cerrar(@PathVariable String id) {
        return ResponseEntity.ok(operacionService.cerrar(id));
    }

    /**
     * POST /operaciones/{id}/cancelar
     * Cancela la operación. El inmueble sigue disponible.
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Operacion> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(operacionService.cancelar(id));
    }
}
