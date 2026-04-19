package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.enums.Finalidad;
import com.proyecto.inmobiliaria.model.enums.TipoInmueble;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de búsquedas avanzadas de inmuebles.
 */
@Service
@RequiredArgsConstructor
public class BusquedaService {

    private final InmuebleRepository inmuebleRepository;
    private final ClienteRepository clienteRepository;

    /**
     * Búsqueda por rango de precios usando el ArbolBST. O(log n + k)
     */
    public List<Inmueble> buscarPorRangoPrecio(double precioMin, double precioMax) {
        if (precioMin > precioMax) {
            throw new RuntimeException("El precio mínimo no puede ser mayor al máximo.");
        }
        return inmuebleRepository.buscarPorRangoPrecio(precioMin, precioMax);
    }

    /**
     * Búsqueda por filtros opcionales. O(n)
     * Cualquier parámetro puede ser null para ignorarlo.
     *
     * @param tipo       filtrar por tipo de inmueble (null = cualquiera)
     * @param ciudad     filtrar por ciudad (null = cualquiera)
     * @param finalidad  filtrar por finalidad VENTA/ARRIENDO (null = cualquiera)
     * @param soloDisponibles si true, excluye inmuebles no disponibles
     */
    public List<Inmueble> buscarPorFiltros(TipoInmueble tipo, String ciudad,
                                            Finalidad finalidad, boolean soloDisponibles) {
        return inmuebleRepository.listarTodos().stream()
                .filter(i -> tipo == null || i.getTipo() == tipo)
                .filter(i -> ciudad == null || i.getCiudad().equalsIgnoreCase(ciudad))
                .filter(i -> finalidad == null || i.getFinalidad() == finalidad)
                .filter(i -> !soloDisponibles || i.isDisponible())
                .collect(Collectors.toList());
    }

    /**
     * Retorna inmuebles compatibles con las preferencias del cliente. O(n)
     * Criterios de compatibilidad:
     *   - Precio ≤ presupuesto del cliente
     *   - Tipo de inmueble coincide con tipoDeseado (si está definido)
     *   - Habitaciones ≥ habitacionesMin del cliente
     *   - Ciudad dentro de sus zonasInteres (si tiene zonas definidas)
     *   - Solo inmuebles disponibles
     */
    public List<Inmueble> buscarCompatiblesConCliente(String idCliente) {
        Cliente cliente = clienteRepository.buscarPorId(idCliente);
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado: " + idCliente);
        }

        return inmuebleRepository.listarTodos().stream()
                .filter(Inmueble::isDisponible)
                .filter(i -> i.getPrecio() <= cliente.getPresupuesto())
                .filter(i -> cliente.getTipoDeseado() == null
                        || i.getTipo() == cliente.getTipoDeseado())
                .filter(i -> i.getHabitaciones() >= cliente.getHabitacionesMin())
                .filter(i -> cliente.getZonasInteres().isEmpty()
                        || cliente.getZonasInteres().contains(i.getCiudad()))
                .collect(Collectors.toList());
    }

    /** Lista todos los inmuebles ordenados por precio ascendente (ArbolBST inorden). O(n) */
    public List<Inmueble> listarOrdenadosPorPrecio() {
        return inmuebleRepository.listarOrdenadosPorPrecio();
    }
}
