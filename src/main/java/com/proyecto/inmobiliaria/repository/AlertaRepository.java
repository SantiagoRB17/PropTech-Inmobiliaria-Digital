package com.proyecto.inmobiliaria.repository;

import com.proyecto.inmobiliaria.model.Alerta;
import com.proyecto.inmobiliaria.model.enums.TipoAlerta;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repositorio de alertas del sistema.
 *
 * Estructuras de datos:
 * 1. HashMap<String, Alerta> → búsqueda O(1) por idAlerta
 * 2. ArrayList<Alerta>       → historial cronológico de alertas generadas
 *
 * Las alertas son generadas por los Services cuando detectan condiciones
 * de negocio (contrato por vencer, inmueble sin visitas, etc.).
 */
@Repository
public class AlertaRepository {

    // ──────────────── Estructuras de datos ────────────────

    /** Almacén principal: idAlerta → Alerta */
    private final Map<String, Alerta> alertas = new HashMap<>();

    /**
     * Historial cronológico de alertas (más reciente al final).
     * ArrayList → append O(1), recorrido cronológico O(n).
     */
    private final List<Alerta> historial = new ArrayList<>();

    // ──────────────── CRUD básico ────────────────

    /**
     * Registra una nueva alerta en el HashMap y en el historial.
     */
    public void guardar(Alerta alerta) {
        alertas.put(alerta.getIdAlerta(), alerta);
        historial.add(alerta);
    }

    /** Búsqueda por ID. O(1) */
    public Alerta buscarPorId(String idAlerta) {
        return alertas.get(idAlerta);
    }

    public List<Alerta> listarTodas() {
        return new ArrayList<>(alertas.values());
    }

    public boolean existePorId(String idAlerta) {
        return alertas.containsKey(idAlerta);
    }

    // ──────────────── Consultas específicas ────────────────

    /**
     * Retorna solo las alertas que aún no han sido resueltas. O(n)
     * Usada por el dashboard para mostrar alertas activas.
     */
    public List<Alerta> listarNoResueltas() {
        return historial.stream()
                .filter(a -> !a.isResuelta())
                .collect(Collectors.toList());
    }

    /**
     * Retorna el historial completo en orden cronológico. O(1)
     */
    public List<Alerta> listarHistorial() {
        return new ArrayList<>(historial);
    }

    /**
     * Verifica si ya existe una alerta SIN resolver para la misma entidad y tipo.
     * Evita duplicar alertas al re-ejecutar la detección. O(n)
     */
    public boolean existeAlertaActivaPara(TipoAlerta tipo, String entidadRelacionada) {
        return historial.stream()
                .anyMatch(a -> !a.isResuelta()
                        && a.getTipo() == tipo
                        && entidadRelacionada.equals(a.getEntidadRelacionada()));
    }

    /**
     * Marca una alerta como resuelta buscándola por ID. O(1)
     */
    public void marcarComoResuelta(String idAlerta) {
        Alerta alerta = alertas.get(idAlerta);
        if (alerta == null) {
            throw new RuntimeException("Alerta no encontrada: " + idAlerta);
        }
        alerta.setResuelta(true);
    }
}
