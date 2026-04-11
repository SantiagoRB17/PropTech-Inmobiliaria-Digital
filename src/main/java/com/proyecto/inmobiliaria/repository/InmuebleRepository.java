package com.proyecto.inmobiliaria.repository;

import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.util.ArbolBST;
import com.proyecto.inmobiliaria.util.PilaAcciones;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de inmuebles. Gestiona tres estructuras de datos:
 */
@Repository
public class InmuebleRepository {

    private final Map<String, Inmueble> inmuebles = new HashMap<>();

    private final ArbolBST arbolPrecios = new ArbolBST();

    private final PilaAcciones<Inmueble> historialCambios = new PilaAcciones<>();

    /**
     * Guarda o actualiza un inmueble.
     */
    public void guardar(Inmueble inmueble) {
        inmuebles.put(inmueble.getCodigo(), inmueble);
        arbolPrecios.insertar(inmueble);
    }

    public Inmueble buscarPorId(String codigo) {
        return inmuebles.get(codigo);
    }

    public void eliminar(String codigo) {
        inmuebles.remove(codigo);
        arbolPrecios.eliminar(codigo);
    }

    public List<Inmueble> listarTodos() {
        return new ArrayList<>(inmuebles.values());
    }

    public boolean existePorId(String codigo) {
        return inmuebles.containsKey(codigo);
    }


    /**
     * Retorna inmuebles cuyo precio esté en [precioMin, precioMax]. O(log n + k)
     */
    public List<Inmueble> buscarPorRangoPrecio(double precioMin, double precioMax) {
        return arbolPrecios.buscarPorRango(precioMin, precioMax);
    }

    /**
     * Retorna todos los inmuebles ordenados de menor a mayor precio. O(n)
     */
    public List<Inmueble> listarOrdenadosPorPrecio() {
        return arbolPrecios.inorden();
    }


    /**
     * Guarda el estado actual del inmueble antes de modificarlo.
     */
    public void guardarEstadoPrevio(Inmueble inmueble) {
        Inmueble copia = copiarInmueble(inmueble);
        historialCambios.push(copia);
    }

    /**
     * Deshace el último cambio: extrae el estado anterior de la pila
     * y lo restaura en el HashMap y en el BST.
     */
    public Inmueble deshacerUltimoCambio() {
        Inmueble estadoPrevio = historialCambios.pop();
        arbolPrecios.eliminar(estadoPrevio.getCodigo());
        inmuebles.put(estadoPrevio.getCodigo(), estadoPrevio);
        arbolPrecios.insertar(estadoPrevio);
        return estadoPrevio;
    }

    public boolean hayAccionesPorDeshacer() {
        return !historialCambios.estaVacia();
    }

    private Inmueble copiarInmueble(Inmueble original) {
        return Inmueble.builder()
                .codigo(original.getCodigo())
                .direccion(original.getDireccion())
                .ciudad(original.getCiudad())
                .barrio(original.getBarrio())
                .tipo(original.getTipo())
                .finalidad(original.getFinalidad())
                .precio(original.getPrecio())
                .area(original.getArea())
                .habitaciones(original.getHabitaciones())
                .banos(original.getBanos())
                .estado(original.getEstado())
                .disponible(original.isDisponible())
                .codigoAsesor(original.getCodigoAsesor())
                .build();
    }
}
