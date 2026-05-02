package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.GrafoRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InmuebleService {

    private final InmuebleRepository inmuebleRepository;
    private final AsesorRepository asesorRepository;
    private final GrafoRepository grafoRepository;

    /**
     * Registra un nuevo inmueble.
     * Valida que el asesor asignado exista y lo indexa en el grafo.
     */
    public Inmueble registrar(Inmueble inmueble) {
        if (inmuebleRepository.existePorId(inmueble.getCodigo())) {
            throw new RuntimeException("Ya existe un inmueble con código: " + inmueble.getCodigo());
        }
        if (!asesorRepository.existePorId(inmueble.getCodigoAsesor())) {
            throw new RuntimeException("Asesor no encontrado: " + inmueble.getCodigoAsesor());
        }
        inmuebleRepository.guardar(inmueble);
        grafoRepository.registrarInmueble(inmueble.getCodigo());

        asesorRepository.buscarPorId(inmueble.getCodigoAsesor())
                .getInmueblesAsignados().add(inmueble.getCodigo());
        return inmueble;
    }

    public Inmueble buscarPorId(String codigo) {
        Inmueble inmueble = inmuebleRepository.buscarPorId(codigo);
        if (inmueble == null) {
            throw new RuntimeException("Inmueble no encontrado: " + codigo);
        }
        return inmueble;
    }

    public List<Inmueble> listarTodos() {
        return inmuebleRepository.listarTodos();
    }

    /**
     * Actualiza los datos de un inmueble guardando el estado previo en la pila
     * para permitir deshacer el cambio.
     */
    public Inmueble actualizar(String codigo, Inmueble datos) {
        Inmueble existente = buscarPorId(codigo);

        inmuebleRepository.guardarEstadoPrevio(existente);

        if (datos.getPrecio() != existente.getPrecio()) {
            inmuebleRepository.registrarCambioPrecio(existente.getCodigo());
        }

        existente.setDireccion(datos.getDireccion());
        existente.setCiudad(datos.getCiudad());
        existente.setBarrio(datos.getBarrio());
        existente.setTipo(datos.getTipo());
        existente.setFinalidad(datos.getFinalidad());
        existente.setPrecio(datos.getPrecio());
        existente.setArea(datos.getArea());
        existente.setHabitaciones(datos.getHabitaciones());
        existente.setBanos(datos.getBanos());
        existente.setEstado(datos.getEstado());
        existente.setDisponible(datos.isDisponible());

        inmuebleRepository.guardar(existente);
        return existente;
    }

    /**
     * Deshace el último cambio realizado sobre cualquier inmueble.
     * Restaura el estado anterior guardado en la PilaAcciones.
     */
    public Inmueble deshacerUltimoCambio() {
        if (!inmuebleRepository.hayAccionesPorDeshacer()) {
            throw new RuntimeException("No hay cambios que deshacer.");
        }
        return inmuebleRepository.deshacerUltimoCambio();
    }

    public void eliminar(String codigo) {
        buscarPorId(codigo);
        inmuebleRepository.eliminar(codigo);
    }

    public void cambiarDisponibilidad(String codigo, boolean disponible) {
        Inmueble inmueble = buscarPorId(codigo);
        inmuebleRepository.guardarEstadoPrevio(inmueble);
        inmueble.setDisponible(disponible);
        inmuebleRepository.guardar(inmueble);
    }


    /** Búsqueda por rango de precios usando el BST. O(log n + k) */
    public List<Inmueble> buscarPorRangoPrecio(double precioMin, double precioMax) {
        if (precioMin > precioMax) {
            throw new RuntimeException("El precio mínimo no puede ser mayor al máximo.");
        }
        return inmuebleRepository.buscarPorRangoPrecio(precioMin, precioMax);
    }

    /** Lista todos los inmuebles ordenados de menor a mayor precio. O(n) */
    public List<Inmueble> listarOrdenadosPorPrecio() {
        return inmuebleRepository.listarOrdenadosPorPrecio();
    }
}
