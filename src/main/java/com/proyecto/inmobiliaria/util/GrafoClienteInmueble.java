package com.proyecto.inmobiliaria.util;

import java.util.*;


public class GrafoClienteInmueble {

    /** Lista de adyacencia: id del nodo → lista de ids de sus vecinos */
    private final Map<String, List<String>> adyacencia;


    public GrafoClienteInmueble() {
        this.adyacencia = new HashMap<>();
    }

    /**
     * Agrega un nodo al grafo si no existe. O(1)
     */
    public void agregarNodo(String id) {
        adyacencia.putIfAbsent(id, new ArrayList<>());
    }

    /**
     * Conecta un cliente con un inmueble (arista no dirigida). O(1)
     * Crea los nodos si no existen. Se llama cuando hay cualquier
     * tipo de interacción cliente-inmueble.
     */
    public void agregarArista(String idCliente, String codigoInmueble) {
        agregarNodo(idCliente);
        agregarNodo(codigoInmueble);
        adyacencia.get(idCliente).add(codigoInmueble);
        adyacencia.get(codigoInmueble).add(idCliente);
    }

    /**
     * Retorna los vecinos directos (inmuebles del cliente o clientes del inmueble). O(1)
     */
    public List<String> obtenerVecinos(String id) {
        return adyacencia.getOrDefault(id, new ArrayList<>());
    }

    /**
     * Verifica si existe una arista entre cliente e inmueble. O(grado del nodo)
     */
    public boolean existeRelacion(String idCliente, String codigoInmueble) {
        return adyacencia.getOrDefault(idCliente, new ArrayList<>()).contains(codigoInmueble);
    }

    /**
     * BFS desde un cliente para recomendar inmuebles. O(V + E)
     * Recorre el grafo en anchura. Los inmuebles que aparecen como vecinos
     * de vecinos del cliente (a 2 saltos) son candidatos a recomendación,
     * excluyendo los inmuebles con los que el cliente ya interactuó.
     *
     * @param idCliente          identificación del cliente al que se recomienda
     * @param maxRecomendaciones tope de resultados a retornar
     * @return lista de códigos de inmuebles recomendados
     */
    public List<String> recomendarInmuebles(String idCliente, int maxRecomendaciones) {
        if (!adyacencia.containsKey(idCliente)) {
            return new ArrayList<>();
        }

        // Inmuebles que el cliente ya conoce (no los volvemos a recomendar)
        Set<String> yaConocidos = new HashSet<>(adyacencia.get(idCliente));
        yaConocidos.add(idCliente);

        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        List<String> recomendaciones = new ArrayList<>();

        cola.add(idCliente);
        visitados.add(idCliente);

        while (!cola.isEmpty() && recomendaciones.size() < maxRecomendaciones) {
            String actual = cola.poll();

            for (String vecino : adyacencia.getOrDefault(actual, new ArrayList<>())) {
                if (visitados.contains(vecino)) continue;
                visitados.add(vecino);
                cola.add(vecino);

                // Solo agregar a recomendaciones si es un inmueble no conocido por el cliente
                boolean esInmueble = !vecino.startsWith("CLI-");
                if (esInmueble && !yaConocidos.contains(vecino)) {
                    recomendaciones.add(vecino);
                    if (recomendaciones.size() >= maxRecomendaciones) {
                        return recomendaciones;
                    }
                }
            }
        }
        return recomendaciones;
    }


    public boolean existeNodo(String id) {
        return adyacencia.containsKey(id);
    }

    public int totalNodos() {
        return adyacencia.size();
    }

    public int totalAristas() {
        return adyacencia.values().stream().mapToInt(List::size).sum() / 2;
    }
}
