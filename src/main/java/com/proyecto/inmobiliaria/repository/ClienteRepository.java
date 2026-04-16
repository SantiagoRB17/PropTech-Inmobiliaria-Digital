package com.proyecto.inmobiliaria.repository;

import com.proyecto.inmobiliaria.model.Cliente;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de clientes.
 * Estructura principal: HashMap<String, Cliente>
 */
@Repository
public class ClienteRepository {

    private final Map<String, Cliente> clientes = new HashMap<>();

    public void guardar(Cliente cliente) {
        clientes.put(cliente.getIdentificacion(), cliente);
    }

    public Cliente buscarPorId(String identificacion) {
        return clientes.get(identificacion);
    }

    public void eliminar(String identificacion) {
        clientes.remove(identificacion);
    }

    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes.values());
    }

    public boolean existePorId(String identificacion) {
        return clientes.containsKey(identificacion);
    }
}
