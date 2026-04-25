package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de clientes.
 * Incluye CRUD + gestión de favoritos, historial de consultas e inmuebles descartados.
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /** POST /clientes — Registra un nuevo cliente. */
    @PostMapping
    public ResponseEntity<Cliente> registrar(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.registrar(cliente));
    }

    /** GET /clientes — Lista todos los clientes. */
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    /** GET /clientes/{id} — Busca un cliente por su identificación. */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    /** PUT /clientes/{id} — Actualiza los datos de un cliente. */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable String id,
                                               @RequestBody Cliente datos) {
        return ResponseEntity.ok(clienteService.actualizar(id, datos));
    }

    /** DELETE /clientes/{id} — Elimina un cliente. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * POST /clientes/{id}/favoritos/{codigoInmueble}
     * Agrega el inmueble a la lista de favoritos del cliente y registra interacción en el grafo.
     */
    @PostMapping("/{id}/favoritos/{codigoInmueble}")
    public ResponseEntity<Void> agregarFavorito(@PathVariable String id,
                                                 @PathVariable String codigoInmueble) {
        clienteService.agregarFavorito(id, codigoInmueble);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /clientes/{id}/favoritos/{codigoInmueble}
     * Quita el inmueble de la lista de favoritos del cliente.
     */
    @DeleteMapping("/{id}/favoritos/{codigoInmueble}")
    public ResponseEntity<Void> quitarFavorito(@PathVariable String id,
                                                @PathVariable String codigoInmueble) {
        clienteService.quitarFavorito(id, codigoInmueble);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /clientes/{id}/consultas/{codigoInmueble}
     * Registra que el cliente consultó el inmueble. Lo agrega al historial y al grafo.
     */
    @PostMapping("/{id}/consultas/{codigoInmueble}")
    public ResponseEntity<Void> registrarConsulta(@PathVariable String id,
                                                   @PathVariable String codigoInmueble) {
        clienteService.registrarConsulta(id, codigoInmueble);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /clientes/{id}/descartados/{codigoInmueble}
     * Descarta el inmueble para este cliente. No aparecerá en recomendaciones futuras.
     */
    @PostMapping("/{id}/descartados/{codigoInmueble}")
    public ResponseEntity<Void> descartarInmueble(@PathVariable String id,
                                                   @PathVariable String codigoInmueble) {
        clienteService.descartarInmueble(id, codigoInmueble);
        return ResponseEntity.ok().build();
    }
}
