package com.proyecto.inmobiliaria.controller;

import com.proyecto.inmobiliaria.model.Alerta;
import com.proyecto.inmobiliaria.service.DeteccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para el módulo de detección de comportamientos inusuales.
 *
 * Permite ejecutar los análisis manualmente desde Postman y consultar
 * los resultados de cada tipo de detección.
 */
@RestController
@RequestMapping("/deteccion")
@RequiredArgsConstructor
public class DeteccionController {

    private final DeteccionService deteccionService;

    /**
     * POST /deteccion/ejecutar
     * Ejecuta todos los módulos de detección y retorna las alertas generadas.
     */
    @PostMapping("/ejecutar")
    public ResponseEntity<List<Alerta>> ejecutarDeteccionCompleta() {
        return ResponseEntity.ok(deteccionService.ejecutarDeteccionCompleta());
    }

    /**
     * GET /deteccion/inmuebles-sin-cierre
     * Detecta inmuebles con muchas visitas sin conversión a operación.
     */
    @GetMapping("/inmuebles-sin-cierre")
    public ResponseEntity<List<Alerta>> detectarInmueblesSinCierre() {
        return ResponseEntity.ok(deteccionService.detectarInmueblesSinCierre());
    }

    /**
     * GET /deteccion/clientes-sin-seguimiento
     * Detecta clientes con muchas visitas sin ninguna operación registrada.
     */
    @GetMapping("/clientes-sin-seguimiento")
    public ResponseEntity<List<Alerta>> detectarClientesSinSeguimiento() {
        return ResponseEntity.ok(deteccionService.detectarClientesSinSeguimiento());
    }

    /**
     * GET /deteccion/asesores-sobrecarga
     * Detecta asesores con una cantidad excesiva de visitas agendadas.
     */
    @GetMapping("/asesores-sobrecarga")
    public ResponseEntity<List<Alerta>> detectarAsesoresConSobrecarga() {
        return ResponseEntity.ok(deteccionService.detectarAsesoresConSobrecarga());
    }

    /**
     * GET /deteccion/cambios-precio-frecuentes
     * Detecta inmuebles cuyo precio ha cambiado demasiadas veces sin cierre.
     */
    @GetMapping("/cambios-precio-frecuentes")
    public ResponseEntity<List<Alerta>> detectarCambiosDePrecioFrecuentes() {
        return ResponseEntity.ok(deteccionService.detectarCambiosDePrecioFrecuentes());
    }

    /**
     * GET /deteccion/concentracion-zona
     * Detecta ciudades con alta concentración de visitas en el período actual.
     */
    @GetMapping("/concentracion-zona")
    public ResponseEntity<List<Alerta>> detectarConcentracionPorZona() {
        return ResponseEntity.ok(deteccionService.detectarConcentracionPorZona());
    }
}
