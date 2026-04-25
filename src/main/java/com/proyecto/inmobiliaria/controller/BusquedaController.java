package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.enums.Finalidad;
import com.proyecto.inmobiliaria.model.enums.TipoInmueble;
import com.proyecto.inmobiliaria.service.BusquedaService;
import com.proyecto.inmobiliaria.service.RecomendacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para búsquedas avanzadas y recomendaciones de inmuebles.
 *
 * Búsquedas disponibles:
 *   - Por rango de precio   → usa ArbolBST       O(log n + k)
 *   - Por filtros            → usa stream filter  O(n)
 *   - Compatibles cliente   → filtra por perfil  O(n)
 *   - Ordenados por precio  → inorden BST         O(n)
 *
 * Recomendaciones:
 *   - BFS sobre GrafoClienteInmueble → O(V+E)
 */
@RestController
@RequiredArgsConstructor
public class BusquedaController {

    private final BusquedaService busquedaService;
    private final RecomendacionService recomendacionService;

    // ── Búsquedas ──

    /**
     * GET /busqueda/rango-precio?precioMin=100000&precioMax=500000
     * Busca inmuebles dentro del rango usando el ArbolBST. O(log n + k)
     */
    @GetMapping("/busqueda/rango-precio")
    public ResponseEntity<List<Inmueble>> buscarPorRangoPrecio(
            @RequestParam double precioMin,
            @RequestParam double precioMax) {
        return ResponseEntity.ok(busquedaService.buscarPorRangoPrecio(precioMin, precioMax));
    }

    /**
     * GET /busqueda/filtros?tipo=APARTAMENTO&ciudad=Bogota&finalidad=VENTA&soloDisponibles=true
     * Filtra inmuebles por cualquier combinación de criterios. Todos los parámetros son opcionales.
     */
    @GetMapping("/busqueda/filtros")
    public ResponseEntity<List<Inmueble>> buscarPorFiltros(
            @RequestParam(required = false) TipoInmueble tipo,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) Finalidad finalidad,
            @RequestParam(defaultValue = "false") boolean soloDisponibles) {
        return ResponseEntity.ok(busquedaService.buscarPorFiltros(tipo, ciudad, finalidad, soloDisponibles));
    }

    /**
     * GET /busqueda/compatibles/{idCliente}
     * Retorna inmuebles disponibles compatibles con el presupuesto, tipo deseado,
     * habitaciones mínimas y zonas de interés del cliente.
     */
    @GetMapping("/busqueda/compatibles/{idCliente}")
    public ResponseEntity<List<Inmueble>> buscarCompatiblesConCliente(@PathVariable String idCliente) {
        return ResponseEntity.ok(busquedaService.buscarCompatiblesConCliente(idCliente));
    }

    /**
     * GET /busqueda/ordenados-por-precio
     * Lista todos los inmuebles ordenados por precio ascendente (recorrido inorden del BST).
     */
    @GetMapping("/busqueda/ordenados-por-precio")
    public ResponseEntity<List<Inmueble>> listarOrdenadosPorPrecio() {
        return ResponseEntity.ok(busquedaService.listarOrdenadosPorPrecio());
    }

    // ── Recomendaciones ──

    /**
     * GET /recomendaciones/{idCliente}?max=5
     * Recomienda inmuebles disponibles usando BFS sobre el grafo de interacciones.
     * Excluye inmuebles ya descartados por el cliente.
     */
    @GetMapping("/recomendaciones/{idCliente}")
    public ResponseEntity<List<Inmueble>> recomendarParaCliente(
            @PathVariable String idCliente,
            @RequestParam(defaultValue = "5") int max) {
        return ResponseEntity.ok(recomendacionService.recomendarParaCliente(idCliente, max));
    }

    /**
     * POST /recomendaciones/interaccion
     * Registra manualmente una interacción cliente-inmueble en el grafo.
     * Body: { "idCliente": "123", "codigoInmueble": "INM-001" }
     */
    @PostMapping("/recomendaciones/interaccion")
    public ResponseEntity<Void> registrarInteraccion(@RequestBody Map<String, String> body) {
        recomendacionService.registrarInteraccion(body.get("idCliente"), body.get("codigoInmueble"));
        return ResponseEntity.ok().build();
    }

    /**
     * GET /recomendaciones/{idCliente}/interacciones
     * Retorna los códigos de inmuebles con los que el cliente ha interactuado.
     */
    @GetMapping("/recomendaciones/{idCliente}/interacciones")
    public ResponseEntity<List<String>> obtenerInteraccionesCliente(@PathVariable String idCliente) {
        return ResponseEntity.ok(recomendacionService.obtenerInteraccionesCliente(idCliente));
    }
}
