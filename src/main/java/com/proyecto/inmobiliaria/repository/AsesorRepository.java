package com.proyecto.inmobiliaria.repository;

import com.proyecto.inmobiliaria.model.Asesor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de asesores.
 * Estructura principal: HashMap<String, Asesor>
 */
@Repository
public class AsesorRepository {

    private final Map<String, Asesor> asesores = new HashMap<>();

    public void guardar(Asesor asesor) {
        asesores.put(asesor.getIdentificacion(), asesor);
    }

    public Asesor buscarPorId(String identificacion) {
        return asesores.get(identificacion);
    }

    public void eliminar(String identificacion) {
        asesores.remove(identificacion);
    }

    public List<Asesor> listarTodos() {
        return new ArrayList<>(asesores.values());
    }

    public boolean existePorId(String identificacion) {
        return asesores.containsKey(identificacion);
    }
}
