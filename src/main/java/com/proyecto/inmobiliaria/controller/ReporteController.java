package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para reportes y rankings del sistema.
 */
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * GET /reportes/asesores-efectividad
     * Retorna los asesores ordenados por cierres realizados (mayor a menor).
     */
    @GetMapping("/asesores-efectividad")
    public ResponseEntity<List<Asesor>> rankingAsesoresPorEfectividad() {
        return ResponseEntity.ok(reporteService.rankingAsesoresPorEfectividad());
    }

    /**
     * GET /reportes/zonas-actividad
     * Retorna las ciudades ordenadas por número de visitas recibidas.
     * Ejemplo: { "Bogotá": 5, "Medellín": 3, "Cali": 1 }
     */
    @GetMapping("/zonas-actividad")
    public ResponseEntity<Map<String, Integer>> rankingZonasPorActividad() {
        return ResponseEntity.ok(reporteService.rankingZonasPorActividad());
    }

    /**
     * GET /reportes/inmuebles-visitas
     * Retorna los inmuebles ordenados por número de visitas recibidas.
     * Ejemplo: { "INM-001": 3, "INM-005": 2 }
     */
    @GetMapping("/inmuebles-visitas")
    public ResponseEntity<Map<String, Integer>> rankingInmueblesPorVisitas() {
        return ResponseEntity.ok(reporteService.rankingInmueblesPorVisitas());
    }
}
