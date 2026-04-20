package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.Visita;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de reportes y rankings del sistema.
 * Reportes disponibles:
 *   - Ranking de asesores por efectividad (cierresRealizados)
 *   - Ranking de zonas por actividad (visitas por ciudad)
 *   - Ranking de inmuebles más visitados
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final AsesorRepository asesorRepository;
    private final InmuebleRepository inmuebleRepository;
    private final VisitaRepository visitaRepository;

    /**
     * Ranking de asesores ordenados por número de cierres realizados (mayor a menor).
     * Útil para identificar los asesores más efectivos de la plataforma.
     */
    public List<Asesor> rankingAsesoresPorEfectividad() {
        List<Asesor> asesores = new ArrayList<>(asesorRepository.listarTodos());

        // Ordenar por cierresRealizados de mayor a menor (burbuja simplificada con Collections.sort)
        Collections.sort(asesores, (a, b) -> b.getCierresRealizados() - a.getCierresRealizados());

        return asesores;
    }

    /**
     * Ranking de zonas (ciudades) por número de visitas recibidas (mayor a menor).
     * Retorna un mapa ordenado: { "Bogotá" -> 5, "Medellín" -> 3, "Cali" -> 1 }
     */
    public Map<String, Integer> rankingZonasPorActividad() {
        // Paso 1: contar visitas por ciudad del inmueble
        Map<String, Integer> conteo = new HashMap<>();
        for (Visita visita : visitaRepository.listarTodas()) {
            Inmueble inmueble = inmuebleRepository.buscarPorId(visita.getCodigoInmueble());
            if (inmueble != null) {
                String ciudad = inmueble.getCiudad();
                int cantidadActual = conteo.getOrDefault(ciudad, 0);
                conteo.put(ciudad, cantidadActual + 1);
            }
        }

        // Paso 2: pasar el mapa a una lista de entradas y ordenarla
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(conteo.entrySet());
        Collections.sort(lista, (a, b) -> b.getValue() - a.getValue());

        // Paso 3: reconstruir el mapa en orden usando LinkedHashMap (mantiene el orden de inserción)
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : lista) {
            resultado.put(entrada.getKey(), entrada.getValue());
        }
        return resultado;
    }

    /**
     * Ranking de inmuebles por número de visitas recibidas (mayor a menor).
     * Retorna un mapa ordenado: { "INM-001" -> 3, "INM-005" -> 2, ... }
     */
    public Map<String, Integer> rankingInmueblesPorVisitas() {
        // Paso 1: contar visitas por código de inmueble
        Map<String, Integer> conteo = new HashMap<>();
        for (Visita visita : visitaRepository.listarTodas()) {
            String codigo = visita.getCodigoInmueble();
            int cantidadActual = conteo.getOrDefault(codigo, 0);
            conteo.put(codigo, cantidadActual + 1);
        }

        // Paso 2: pasar el mapa a una lista de entradas y ordenarla
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(conteo.entrySet());
        Collections.sort(lista, (a, b) -> b.getValue() - a.getValue());

        // Paso 3: reconstruir el mapa en orden
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entrada : lista) {
            resultado.put(entrada.getKey(), entrada.getValue());
        }
        return resultado;
    }
}
