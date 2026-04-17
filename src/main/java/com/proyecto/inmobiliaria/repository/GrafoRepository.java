package com.proyecto.inmobiliaria.repository;

import com.proyecto.inmobiliaria.util.GrafoClienteInmueble;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio del grafo de relaciones cliente-inmueble.
 * Estructura: GrafoClienteInmueble (lista de adyacencia, no dirigido)
 * → aristas se crean con cada interacción cliente-inmueble
 * → BFS O(V+E) para recomendaciones
 * Convención de IDs en el grafo:
 *   - Clientes:  prefijo "CLI-"  (ej: "CLI-123456789")
 *   - Inmuebles: prefijo "INM-"  (ej: "INM-001")
 */
@Repository
public class GrafoRepository {

    private final GrafoClienteInmueble grafo = new GrafoClienteInmueble();

    /** Registra un cliente como nodo en el grafo. */
    public void registrarCliente(String idCliente) {
        grafo.agregarNodo("CLI-" + idCliente);
    }

    /** Registra un inmueble como nodo en el grafo. */
    public void registrarInmueble(String codigoInmueble) {
        grafo.agregarNodo(codigoInmueble);
    }

    /*
     * Crea una arista entre cliente e inmueble (interacción registrada).
     * Se llama cuando el cliente consulta, guarda, visita o negocia un inmueble.
     */
    public void registrarInteraccion(String idCliente, String codigoInmueble) {
        grafo.agregarArista("CLI-" + idCliente, codigoInmueble);
    }

    public boolean existeInteraccion(String idCliente, String codigoInmueble) {
        return grafo.existeRelacion("CLI-" + idCliente, codigoInmueble);
    }


    /**
     * Retorna hasta {@code maxRecomendaciones} códigos de inmuebles recomendados
     * para el cliente, usando BFS sobre el grafo. O(V + E)
     */
    public List<String> recomendarInmuebles(String idCliente, int maxRecomendaciones) {
        return grafo.recomendarInmuebles("CLI-" + idCliente, maxRecomendaciones);
    }

    /** Retorna los inmuebles con los que el cliente ha interactuado. */
    public List<String> obtenerInmueblesDelCliente(String idCliente) {
        return grafo.obtenerVecinos("CLI-" + idCliente);
    }

    public int totalNodos() {
        return grafo.totalNodos();
    }

    public int totalAristas() {
        return grafo.totalAristas();
    }
}
