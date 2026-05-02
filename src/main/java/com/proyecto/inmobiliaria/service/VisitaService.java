package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Visita;
import com.proyecto.inmobiliaria.model.enums.EstadoBusqueda;
import com.proyecto.inmobiliaria.model.enums.EstadoVisita;
import com.proyecto.inmobiliaria.model.enums.NivelAtencion;
import com.proyecto.inmobiliaria.model.enums.TipoAlerta;
import com.proyecto.inmobiliaria.model.enums.TipoCliente;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final ClienteRepository clienteRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AsesorRepository asesorRepository;
    private final AlertaService alertaService;

    /**
     * Procesa una solicitud de visita:
     * 1. Valida inmueble, cliente, asesor
     * 2. Genera un ID si no tiene
     * 3. Calcula la prioridad según el perfil del cliente
     * 4. Encola en la ColaAtencion
     */
    public Visita solicitarVisita(Visita visita) {
        validar(visita);

        if (visita.getIdVisita() == null || visita.getIdVisita().isBlank()) {
            visita.setIdVisita(UUID.randomUUID().toString());
        }

        Cliente cliente = clienteRepository.buscarPorId(visita.getIdCliente());
        int prioridad = calcularPrioridad(cliente);
        visita.setPrioridad(prioridad);
        visita.setEstado(EstadoVisita.PENDIENTE);

        visitaRepository.encolarSolicitud(visita);

        Asesor asesor = asesorRepository.buscarPorId(visita.getIdAsesor());
        if (asesor.getVisitasAgendadas() == null) {
            asesor.setVisitasAgendadas(new java.util.ArrayList<>());
        }
        asesor.getVisitasAgendadas().add(visita.getIdVisita());

        return visita;
    }

    /**
     * Atiende la siguiente solicitud en orden de llegada.
     * La visita atendida pasa a la cola de prioridad para ser despachada.
     */
    public Visita atenderSiguienteSolicitud() {
        if (!visitaRepository.haySolicitudesPendientes()) {
            throw new RuntimeException("No hay solicitudes de visita pendientes en la cola.");
        }
        Visita visita = visitaRepository.atenderSiguienteSolicitud();
        visitaRepository.encolarConPrioridad(visita);
        return visita;
    }

    /**
     * Despacha la visita con mayor prioridad del heap.
     * La marca como CONFIRMADA.
     */
    public Visita despacharVisitaPrioritaria() {
        if (!visitaRepository.hayVisitasPrioritarias()) {
            throw new RuntimeException("No hay visitas en la cola de prioridad.");
        }
        Visita visita = visitaRepository.atenderVisitaPrioritaria();
        visita.setEstado(EstadoVisita.CONFIRMADA);
        visitaRepository.guardar(visita);
        return visita;
    }

    public Visita buscarPorId(String idVisita) {
        Visita visita = visitaRepository.buscarPorId(idVisita);
        if (visita == null) {
            throw new RuntimeException("Visita no encontrada: " + idVisita);
        }
        return visita;
    }

    public List<Visita> listarTodas() {
        return visitaRepository.listarTodas();
    }

    public List<Visita> listarVisitasPrioritarias() {
        return visitaRepository.listarVisitasPrioritarias();
    }

    public Visita actualizarEstado(String idVisita, EstadoVisita nuevoEstado) {
        Visita visita = buscarPorId(idVisita);
        visita.setEstado(nuevoEstado);
        visitaRepository.guardar(visita);

        if (nuevoEstado == EstadoVisita.PENDIENTE) {
            alertaService.generarAlerta(
                    TipoAlerta.VISITA_PENDIENTE,
                    "Visita " + idVisita + " sigue pendiente de confirmación.",
                    NivelAtencion.MEDIA,
                    idVisita
            );
        }
        return visita;
    }


    /**
     * Calcula el puntaje de prioridad de la visita según el perfil del cliente.
     * Criterios:
     *   - Cliente VIP                          → +10
     *   - Cliente con búsqueda ACTIVA          → +5  (alta intención de cierre)
     *   - Base para todos                      → +3
     */
    private int calcularPrioridad(Cliente cliente) {
        int prioridad = 3;

        if (cliente.getTipoCliente() == TipoCliente.VIP) {
            prioridad += 10;
        }
        if (cliente.getEstadoBusqueda() == EstadoBusqueda.ACTIVO) {
            prioridad += 5;
        }
        return prioridad;
    }


    private void validar(Visita visita) {
        if (!inmuebleRepository.existePorId(visita.getCodigoInmueble())) {
            throw new RuntimeException("Inmueble no encontrado: " + visita.getCodigoInmueble());
        }
        if (clienteRepository.buscarPorId(visita.getIdCliente()) == null) {
            throw new RuntimeException("Cliente no encontrado: " + visita.getIdCliente());
        }
        if (!asesorRepository.existePorId(visita.getIdAsesor())) {
            throw new RuntimeException("Asesor no encontrado: " + visita.getIdAsesor());
        }
    }
}
