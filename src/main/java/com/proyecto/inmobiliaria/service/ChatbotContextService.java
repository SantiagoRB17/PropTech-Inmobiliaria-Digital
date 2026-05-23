package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.dto.ChatbotContextDTO;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.enums.EstadoVisita;
import com.proyecto.inmobiliaria.repository.AlertaRepository;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Construye el snapshot del sistema para que n8n lo use como contexto.
 * Recorre las estructuras en memoria: HashMap de inmuebles/clientes,
 * cola de visitas y lista de alertas, más el rango de precios del BST.
 */
@Service
@RequiredArgsConstructor
public class ChatbotContextService {

    private final InmuebleRepository inmuebleRepository;
    private final ClienteRepository clienteRepository;
    private final VisitaRepository visitaRepository;
    private final AlertaRepository alertaRepository;

    /**
     * Genera el contexto completo del sistema. O(n) sobre cada colección.
     */
    public ChatbotContextDTO buildContext() {

        // Inmuebles disponibles con campos clave
        List<ChatbotContextDTO.InmuebleResumen> inmueblesDisponibles = inmuebleRepository.listarTodos().stream()
                .filter(Inmueble::isDisponible)
                .map(i -> new ChatbotContextDTO.InmuebleResumen(
                        i.getCodigo(),
                        i.getTipo().name(),
                        i.getFinalidad().name(),
                        i.getBarrio() + ", " + i.getCiudad(),
                        i.getPrecio(),
                        i.getHabitaciones(),
                        i.getCodigoAsesor()
                ))
                .toList();

        // Clientes con sus preferencias de búsqueda
        List<ChatbotContextDTO.ClienteResumen> clientes = clienteRepository.listarTodos().stream()
                .map(c -> new ChatbotContextDTO.ClienteResumen(
                        c.getIdentificacion(),
                        c.getNombre(),
                        c.getPresupuesto(),
                        c.getTipoDeseado() != null ? c.getTipoDeseado().name() : "NO_ESPECIFICADO",
                        c.getZonasInteres()
                ))
                .toList();

        // Visitas en estado PENDIENTE (aún no confirmadas ni realizadas)
        List<ChatbotContextDTO.VisitaResumen> visitasPendientes = visitaRepository.listarTodas().stream()
                .filter(v -> v.getEstado() == EstadoVisita.PENDIENTE)
                .map(v -> new ChatbotContextDTO.VisitaResumen(
                        v.getIdVisita(),
                        v.getCodigoInmueble(),
                        v.getIdCliente(),
                        v.getEstado().name()
                ))
                .toList();

        // Visitas con prioridad asignada (extraídas del Max-Heap)
        List<ChatbotContextDTO.VisitaResumen> visitasPrioritarias = visitaRepository.listarVisitasPrioritarias().stream()
                .map(v -> new ChatbotContextDTO.VisitaResumen(
                        v.getIdVisita(),
                        v.getCodigoInmueble(),
                        v.getIdCliente(),
                        v.getEstado().name()
                ))
                .toList();

        // Alertas activas (no resueltas) del sistema
        List<ChatbotContextDTO.AlertaResumen> alertasActivas = alertaRepository.listarNoResueltas().stream()
                .map(a -> new ChatbotContextDTO.AlertaResumen(
                        a.getIdAlerta(),
                        a.getTipo().name(),
                        a.getDescripcion(),
                        a.getNivel().name()
                ))
                .toList();

        // Rango de precios usando inorden del ArbolBST (ya ordenado de menor a mayor)
        List<Inmueble> ordenados = inmuebleRepository.listarOrdenadosPorPrecio();
        ChatbotContextDTO.RangoPrecio resumenRangoPrecios;
        if (ordenados.isEmpty()) {
            resumenRangoPrecios = new ChatbotContextDTO.RangoPrecio(0, 0);
        } else {
            resumenRangoPrecios = new ChatbotContextDTO.RangoPrecio(
                    ordenados.get(0).getPrecio(),
                    ordenados.get(ordenados.size() - 1).getPrecio()
            );
        }

        return new ChatbotContextDTO(
                inmueblesDisponibles.size(),
                inmueblesDisponibles,
                clientes.size(),
                clientes,
                visitasPendientes,
                visitasPrioritarias,
                alertasActivas.size(),
                alertasActivas,
                resumenRangoPrecios,
                LocalDateTime.now().toString()
        );
    }
}
