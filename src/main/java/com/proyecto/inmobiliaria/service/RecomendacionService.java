package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.GrafoRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de recomendaciones de inmuebles.
 * Usa el GrafoClienteInmueble (BFS O(V+E)) para encontrar inmuebles
 * que otros clientes con intereses similares han consultado.
 * Luego filtra para retornar solo inmuebles disponibles.
 */
@Service
@RequiredArgsConstructor
public class RecomendacionService {

    private final GrafoRepository grafoRepository;
    private final InmuebleRepository inmuebleRepository;
    private final ClienteRepository clienteRepository;

    /**
     * Recomienda inmuebles disponibles para un cliente usando BFS sobre el grafo.
     * Flujo:
     *   1. BFS desde el nodo-cliente → lista de códigos candidatos
     *   2. Buscar cada código en el repositorio
     *   3. Filtrar: solo disponibles y no descartados por el cliente
     *
     * @param idCliente          identificación del cliente
     * @param maxRecomendaciones tope de resultados
     * @return lista de inmuebles recomendados
     */
    public List<Inmueble> recomendarParaCliente(String idCliente, int maxRecomendaciones) {
        if (clienteRepository.buscarPorId(idCliente) == null) {
            throw new RuntimeException("Cliente no encontrado: " + idCliente);
        }

        Cliente cliente = clienteRepository.buscarPorId(idCliente);
        List<String> descartados = cliente.getInmueblesDescartados();

        // BFS retorna más candidatos de los necesarios para poder filtrar
        List<String> candidatos = grafoRepository.recomendarInmuebles(idCliente, maxRecomendaciones * 3);

        return candidatos.stream()
                .map(inmuebleRepository::buscarPorId)
                .filter(inmueble -> inmueble != null)
                .filter(Inmueble::isDisponible)
                .filter(inmueble -> !descartados.contains(inmueble.getCodigo()))
                .limit(maxRecomendaciones)
                .collect(Collectors.toList());
    }

    /**
     * Registra manualmente una interacción cliente-inmueble en el grafo.
     */
    public void registrarInteraccion(String idCliente, String codigoInmueble) {
        if (clienteRepository.buscarPorId(idCliente) == null) {
            throw new RuntimeException("Cliente no encontrado: " + idCliente);
        }
        if (!inmuebleRepository.existePorId(codigoInmueble)) {
            throw new RuntimeException("Inmueble no encontrado: " + codigoInmueble);
        }
        grafoRepository.registrarInteraccion(idCliente, codigoInmueble);
    }

    /** Retorna los códigos de inmuebles con los que el cliente ha interactuado. */
    public List<String> obtenerInteraccionesCliente(String idCliente) {
        return grafoRepository.obtenerInmueblesDelCliente(idCliente);
    }
}
