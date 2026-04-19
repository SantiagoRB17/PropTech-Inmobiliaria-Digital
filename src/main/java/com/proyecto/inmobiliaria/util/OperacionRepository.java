package com.proyecto.inmobiliaria.util;

import com.proyecto.inmobiliaria.model.Operacion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de operaciones comerciales.
 *
 * Estructuras de datos:
 * 1. HashMap<String, Operacion> → búsqueda O(1) por idOperacion
 * 2. ArrayList<Operacion>       → historial cronológico (orden de registro)
 *
 * El ArrayList complementa al HashMap: el mapa permite acceso rápido por ID,
 * mientras que la lista preserva el orden de inserción para reportes históricos.
 */
@Repository
public class OperacionRepository {


    /** Almacén principal: idOperacion → Operacion */
    private final Map<String, Operacion> operaciones = new HashMap<>();

    /**
     * Historial cronológico de operaciones (más reciente al final).
     * ArrayList → acceso por índice O(1), append O(1).
     */
    private final List<Operacion> historial = new ArrayList<>();


    /**
     * Registra una operación. La agrega al HashMap y al historial cronológico.
     */
    public void guardar(Operacion operacion) {
        operaciones.put(operacion.getIdOperacion(), operacion);
        historial.add(operacion);
    }

    /** Búsqueda por ID. O(1) */
    public Operacion buscarPorId(String idOperacion) {
        return operaciones.get(idOperacion);
    }

    public List<Operacion> listarTodas() {
        return new ArrayList<>(operaciones.values());
    }

    public boolean existePorId(String idOperacion) {
        return operaciones.containsKey(idOperacion);
    }


    /**
     * Retorna las operaciones en orden de registro (más antigua primero). O(1)

     */
    public List<Operacion> listarHistorial() {
        return new ArrayList<>(historial);
    }
}
