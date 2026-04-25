package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.service.AsesorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de asesores inmobiliarios.
 * CRUD + asignación de inmuebles.
 */
@RestController
@RequestMapping("/asesores")
@RequiredArgsConstructor
public class AsesorController {

    private final AsesorService asesorService;

    /** POST /asesores — Registra un nuevo asesor. */
    @PostMapping
    public ResponseEntity<Asesor> registrar(@RequestBody Asesor asesor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(asesorService.registrar(asesor));
    }

    /** GET /asesores — Lista todos los asesores. */
    @GetMapping
    public ResponseEntity<List<Asesor>> listarTodos() {
        return ResponseEntity.ok(asesorService.listarTodos());
    }

    /** GET /asesores/{id} — Busca un asesor por su identificación. */
    @GetMapping("/{id}")
    public ResponseEntity<Asesor> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(asesorService.buscarPorId(id));
    }

    /** PUT /asesores/{id} — Actualiza los datos de un asesor. */
    @PutMapping("/{id}")
    public ResponseEntity<Asesor> actualizar(@PathVariable String id,
                                              @RequestBody Asesor datos) {
        return ResponseEntity.ok(asesorService.actualizar(id, datos));
    }

    /** DELETE /asesores/{id} — Elimina un asesor. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        asesorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /asesores/{id}/inmuebles/{codigoInmueble}
     * Asigna un inmueble existente al portafolio del asesor.
     */
    @PostMapping("/{id}/inmuebles/{codigoInmueble}")
    public ResponseEntity<Void> asignarInmueble(@PathVariable String id,
                                                 @PathVariable String codigoInmueble) {
        asesorService.asignarInmueble(id, codigoInmueble);
        return ResponseEntity.ok().build();
    }
}
