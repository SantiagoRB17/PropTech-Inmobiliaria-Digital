package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.GrafoRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final InmuebleRepository inmuebleRepository;
    private final GrafoRepository grafoRepository;

    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existePorId(cliente.getIdentificacion())) {
            throw new RuntimeException("Ya existe un cliente con identificación: " + cliente.getIdentificacion());
        }
        // Cuando llega vía JSON, Jackson usa @NoArgsConstructor y @Builder.Default no aplica → listas null
        if (cliente.getZonasInteres() == null)          cliente.setZonasInteres(new ArrayList<>());
        if (cliente.getFavoritos() == null)              cliente.setFavoritos(new ArrayList<>());
        if (cliente.getInmueblesConsultados() == null)   cliente.setInmueblesConsultados(new ArrayList<>());
        if (cliente.getInmueblesDescartados() == null)   cliente.setInmueblesDescartados(new ArrayList<>());
        if (cliente.getInmueblesNegociados() == null)    cliente.setInmueblesNegociados(new ArrayList<>());
        clienteRepository.guardar(cliente);
        grafoRepository.registrarCliente(cliente.getIdentificacion());
        return cliente;
    }

    public Cliente buscarPorId(String identificacion) {
        Cliente cliente = clienteRepository.buscarPorId(identificacion);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado: " + identificacion);
        }
        return cliente;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Cliente actualizar(String identificacion, Cliente datos) {
        Cliente existente = buscarPorId(identificacion);
        existente.setNombre(datos.getNombre());
        existente.setCorreo(datos.getCorreo());
        existente.setTelefono(datos.getTelefono());
        existente.setTipoCliente(datos.getTipoCliente());
        existente.setPresupuesto(datos.getPresupuesto());
        existente.setZonasInteres(datos.getZonasInteres());
        existente.setTipoDeseado(datos.getTipoDeseado());
        existente.setHabitacionesMin(datos.getHabitacionesMin());
        existente.setEstadoBusqueda(datos.getEstadoBusqueda());
        clienteRepository.guardar(existente);
        return existente;
    }

    public void eliminar(String identificacion) {
        buscarPorId(identificacion);
        clienteRepository.eliminar(identificacion);
    }


    /**
     * Registra que el cliente consultó un inmueble.
     * Lo agrega a su historial y crea una arista en el grafo.
     */
    public void registrarConsulta(String idCliente, String codigoInmueble) {
        Cliente cliente = buscarPorId(idCliente);
        validarInmuebleExiste(codigoInmueble);

        if (!cliente.getInmueblesConsultados().contains(codigoInmueble)) {
            cliente.getInmueblesConsultados().add(codigoInmueble);
        }
        grafoRepository.registrarInteraccion(idCliente, codigoInmueble);
    }

    /**
     * Agrega un inmueble a los favoritos del cliente.
     * También registra la interacción en el grafo.
     */
    public void agregarFavorito(String idCliente, String codigoInmueble) {
        Cliente cliente = buscarPorId(idCliente);
        validarInmuebleExiste(codigoInmueble);

        if (!cliente.getFavoritos().contains(codigoInmueble)) {
            cliente.getFavoritos().add(codigoInmueble);
            grafoRepository.registrarInteraccion(idCliente, codigoInmueble);
        }
    }

    public void quitarFavorito(String idCliente, String codigoInmueble) {
        Cliente cliente = buscarPorId(idCliente);
        cliente.getFavoritos().remove(codigoInmueble);
    }

    /**
     * Marca un inmueble como descartado por el cliente.
     * El RecomendacionService lo excluirá de futuras sugerencias.
     */
    public void descartarInmueble(String idCliente, String codigoInmueble) {
        Cliente cliente = buscarPorId(idCliente);
        validarInmuebleExiste(codigoInmueble);

        if (!cliente.getInmueblesDescartados().contains(codigoInmueble)) {
            cliente.getInmueblesDescartados().add(codigoInmueble);
        }
        cliente.getFavoritos().remove(codigoInmueble);
    }


    private void validarInmuebleExiste(String codigoInmueble) {
        if (!inmuebleRepository.existePorId(codigoInmueble)) {
            throw new RuntimeException("Inmueble no encontrado: " + codigoInmueble);
        }
    }
}
