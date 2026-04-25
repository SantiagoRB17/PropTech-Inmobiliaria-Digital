package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.service.InmuebleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de inmuebles.
 * Expone operaciones CRUD + funciones especiales (deshacer, disponibilidad).
 */
@RestController
@RequestMapping("/inmuebles")
@RequiredArgsConstructor
public class InmuebleController {

    private final InmuebleService inmuebleService;

    /** POST /inmuebles — Registra un nuevo inmueble. */
    @PostMapping
    public ResponseEntity<Inmueble> registrar(@RequestBody Inmueble inmueble) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inmuebleService.registrar(inmueble));
    }

    /** GET /inmuebles — Lista todos los inmuebles. */
    @GetMapping
    public ResponseEntity<List<Inmueble>> listarTodos() {
        return ResponseEntity.ok(inmuebleService.listarTodos());
    }

    /** GET /inmuebles/{codigo} — Busca un inmueble por su código. */
    @GetMapping("/{codigo}")
    public ResponseEntity<Inmueble> buscarPorId(@PathVariable String codigo) {
        return ResponseEntity.ok(inmuebleService.buscarPorId(codigo));
    }

    /**
     * GET /inmuebles/ordenados-por-precio
     * Retorna todos los inmuebles ordenados por precio ascendente (recorrido inorden del BST).
     */
    @GetMapping("/ordenados-por-precio")
    public ResponseEntity<List<Inmueble>> listarOrdenadosPorPrecio() {
        return ResponseEntity.ok(inmuebleService.listarOrdenadosPorPrecio());
    }

    /** PUT /inmuebles/{codigo} — Actualiza los datos de un inmueble. Guarda estado previo en la pila. */
    @PutMapping("/{codigo}")
    public ResponseEntity<Inmueble> actualizar(@PathVariable String codigo,
                                                @RequestBody Inmueble datos) {
        return ResponseEntity.ok(inmuebleService.actualizar(codigo, datos));
    }

    /** DELETE /inmuebles/{codigo} — Elimina un inmueble. */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable String codigo) {
        inmuebleService.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /inmuebles/deshacer
     * Restaura el último estado guardado de cualquier inmueble (PilaAcciones LIFO).
     */
    @PostMapping("/deshacer")
    public ResponseEntity<Inmueble> deshacerUltimoCambio() {
        return ResponseEntity.ok(inmuebleService.deshacerUltimoCambio());
    }

    /**
     * PATCH /inmuebles/{codigo}/disponibilidad
     * Cambia la disponibilidad del inmueble (true/false).
     * Body: { "disponible": true }
     */
    @PatchMapping("/{codigo}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(@PathVariable String codigo,
                                                       @RequestBody Map<String, Boolean> body) {
        boolean disponible = body.get("disponible");
        inmuebleService.cambiarDisponibilidad(codigo, disponible);
        return ResponseEntity.ok().build();
    }
}
