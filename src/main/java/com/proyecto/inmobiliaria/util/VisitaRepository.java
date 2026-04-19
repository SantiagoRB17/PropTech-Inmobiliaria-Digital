package com.proyecto.inmobiliaria.util;

import com.proyecto.inmobiliaria.model.Visita;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de visitas. Gestiona tres estructuras de datos:
 *
 * 1. HashMap<String, Visita>    -> búsqueda O(1) por idVisita
 * 2. ColaAtencion<Visita>       -> solicitudes FIFO (orden de llegada)
 * 3. ColaPrioridadVisitas       -> despacho por prioridad (VIP, urgentes, etc.)
 *
 * Flujo típico:
 *   - El cliente solicita una visita -> se encola en ColaAtencion.
 *   - El asesor asigna prioridad -> la visita se mueve a ColaPrioridadVisitas.
 *   - Al momento de despachar, se extrae la de mayor prioridad.
 */
@Repository
public class VisitaRepository {

    // ──────────────── Estructuras de datos ────────────────

    /** Almacén principal: idVisita → Visita */
    private final Map<String, Visita> visitas = new HashMap<>();


    private final ColaAtencion<Visita> colaAtencion = new ColaAtencion<>();

    /** Max-Heap: visitas ordenadas por prioridad de negocio */
    private final ColaPrioridadVisitas colaPrioridad = new ColaPrioridadVisitas();



    public void guardar(Visita visita) {
        visitas.put(visita.getIdVisita(), visita);
    }

    /** Búsqueda por ID. O(1) */
    public Visita buscarPorId(String idVisita) {
        return visitas.get(idVisita);
    }

    public void eliminar(String idVisita) {
        visitas.remove(idVisita);
    }

    public List<Visita> listarTodas() {
        return new ArrayList<>(visitas.values());
    }

    public boolean existePorId(String idVisita) {
        return visitas.containsKey(idVisita);
    }


    /**
     * Encola una solicitud de visita en orden de llegada.
     * También la guarda en el HashMap para búsqueda por ID.
     */
    public void encolarSolicitud(Visita visita) {
        guardar(visita);
        colaAtencion.encolar(visita);
    }

    /**
     * Atiende la siguiente solicitud en orden de llegada (FIFO).
     * Retorna la visita que debe ser procesada.
     */
    public Visita atenderSiguienteSolicitud() {
        return colaAtencion.desencolar();
    }

    public Visita verProximaSolicitud() {
        return colaAtencion.verFrente();
    }

    public boolean haySolicitudesPendientes() {
        return !colaAtencion.estaVacia();
    }

    public int totalSolicitudesEnCola() {
        return colaAtencion.tamanio();
    }


    /**
     * Encola una visita en el heap de prioridad.
     * La visita debe tener su campo {@code prioridad} ya calculado por el Service.
     */
    public void encolarConPrioridad(Visita visita) {
        colaPrioridad.insertar(visita);
    }

    /**
     * Extrae y retorna la visita con mayor prioridad.
     * Usada para despachar al asesor la visita más importante primero.
     */
    public Visita atenderVisitaPrioritaria() {
        return colaPrioridad.extraerMaximo();
    }

    public Visita verProximaVisitaPrioritaria() {
        return colaPrioridad.verMaximo();
    }

    public boolean hayVisitasPrioritarias() {
        return !colaPrioridad.estaVacia();
    }

    public int totalVisitasPrioritarias() {
        return colaPrioridad.tamanio();
    }

    /** Retorna todas las visitas en el heap (sin orden garantizado). */
    public List<Visita> listarVisitasPrioritarias() {
        return colaPrioridad.obtenerTodos();
    }
}
