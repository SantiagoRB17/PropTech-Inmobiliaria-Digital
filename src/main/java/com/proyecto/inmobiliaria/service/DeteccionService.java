package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Alerta;
import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Visita;
import com.proyecto.inmobiliaria.model.enums.NivelAtencion;
import com.proyecto.inmobiliaria.model.enums.TipoAlerta;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.VisitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service de detección de comportamientos comerciales inusuales.
 * Detecta patrones sospechosos y genera alertas automáticas:
 *   - Inmuebles con muchas visitas pero sin cierre
 *   - Clientes que agendan múltiples visitas sin registrar ninguna operación
 *   - Asesores con sobrecarga de visitas asignadas
 * Umbrales:
 *   UMBRAL_VISITAS_INMUEBLE = 3 visitas → alerta INMUEBLE_SIN_CIERRE
 *   UMBRAL_VISITAS_CLIENTE  = 3 visitas → alerta CLIENTE_SIN_SEGUIMIENTO
 *   UMBRAL_VISITAS_ASESOR   = 5 visitas → alerta de sobrecarga
 */
@Service
@RequiredArgsConstructor
public class DeteccionService {

    private static final int UMBRAL_VISITAS_INMUEBLE = 3;
    private static final int UMBRAL_VISITAS_CLIENTE  = 3;
    private static final int UMBRAL_VISITAS_ASESOR   = 5;

    private final VisitaRepository visitaRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AsesorRepository asesorRepository;
    private final ClienteRepository clienteRepository;
    private final AlertaService alertaService;

    /**
     * Ejecuta todos los módulos de detección a la vez.
     * Retorna la lista de alertas generadas en esta ejecución.
     */
    public List<Alerta> ejecutarDeteccionCompleta() {
        List<Alerta> alertasGeneradas = new ArrayList<>();
        alertasGeneradas.addAll(detectarInmueblesSinCierre());
        alertasGeneradas.addAll(detectarClientesSinSeguimiento());
        alertasGeneradas.addAll(detectarAsesoresConSobrecarga());
        return alertasGeneradas;
    }

    /**i
     * Detecta inmuebles con muchas visitas que siguen disponibles.
     * Criterio: >= UMBRAL_VISITAS_INMUEBLE visitas y el inmueble todavía está dsponible.
     */
    public List<Alerta> detectarInmueblesSinCierre() {
        List<Alerta> alertas = new ArrayList<>();

        // Paso 1: contar cuántas visitas tiene cada inmueble
        Map<String, Integer> visitasPorInmueble = contarVisitasPorInmueble();

        // Paso 2: revisar los que superan el umbral
        for (Map.Entry<String, Integer> entrada : visitasPorInmueble.entrySet()) {
            String codigo = entrada.getKey();
            int cantidad = entrada.getValue();

            if (cantidad >= UMBRAL_VISITAS_INMUEBLE
                    && inmuebleRepository.existePorId(codigo)
                    && inmuebleRepository.buscarPorId(codigo).isDisponible()
                    && !alertaService.existeAlertaActivaPara(TipoAlerta.INMUEBLE_SIN_CIERRE, codigo)) {

                alertas.add(alertaService.generarAlerta(
                        TipoAlerta.INMUEBLE_SIN_CIERRE,
                        "Inmueble " + codigo + " acumula " + cantidad
                                + " visitas sin cierre. Alta demanda sin conversión.",
                        NivelAtencion.MEDIA,
                        codigo
                ));
            }
        }
        return alertas;
    }

    /**
     * Detecta clientes con muchas visitas pero sin ninguna operación registrada.
     * Criterio: >= UMBRAL_VISITAS_CLIENTE visitas e inmueblesNegociados vacío.
     */
    public List<Alerta> detectarClientesSinSeguimiento() {
        List<Alerta> alertas = new ArrayList<>();

        // Paso 1: contar cuántas visitas ha agendado cada cliente
        Map<String, Integer> visitasPorCliente = contarVisitasPorCliente();

        // Paso 2: revisar los que superan el umbral sin haber negociado nada
        for (Map.Entry<String, Integer> entrada : visitasPorCliente.entrySet()) {
            String idCliente = entrada.getKey();
            int cantidad = entrada.getValue();

            Cliente cliente = clienteRepository.buscarPorId(idCliente);
            if (cliente == null) continue;

            if (cantidad >= UMBRAL_VISITAS_CLIENTE
                    && cliente.getInmueblesNegociados().isEmpty()
                    && !alertaService.existeAlertaActivaPara(TipoAlerta.CLIENTE_SIN_SEGUIMIENTO, idCliente)) {

                alertas.add(alertaService.generarAlerta(
                        TipoAlerta.CLIENTE_SIN_SEGUIMIENTO,
                        "Cliente " + cliente.getNombre() + " (" + idCliente + ") tiene "
                                + cantidad + " visitas sin ninguna operación registrada.",
                        NivelAtencion.MEDIA,
                        idCliente
                ));
            }
        }
        return alertas;
    }

    /**
     * Detecta asesores con demasiadas visitas agendadas.
     * Criterio: asesor con >= UMBRAL_VISITAS_ASESOR visitas en su lista visitasAgendadas.
     */
    public List<Alerta> detectarAsesoresConSobrecarga() {
        List<Alerta> alertas = new ArrayList<>();

        for (Asesor asesor : asesorRepository.listarTodos()) {
            int cantidad = asesor.getVisitasAgendadas().size();
            if (cantidad >= UMBRAL_VISITAS_ASESOR
                    && !alertaService.existeAlertaActivaPara(TipoAlerta.CLIENTE_SIN_SEGUIMIENTO, asesor.getIdentificacion())) {
                alertas.add(alertaService.generarAlerta(
                        TipoAlerta.CLIENTE_SIN_SEGUIMIENTO,
                        "Asesor " + asesor.getNombre() + " (" + asesor.getIdentificacion()
                                + ") tiene " + cantidad + " visitas agendadas. Posible sobrecarga.",
                        NivelAtencion.ALTA,
                        asesor.getIdentificacion()
                ));
            }
        }
        return alertas;
    }

    /**
     * Recorre todas las visitas y cuenta cuántas hay por cada código de inmueble.
     * Usa HashMap para acumulación: O(n) en el número de visitas.
     */
    private Map<String, Integer> contarVisitasPorInmueble() {
        Map<String, Integer> conteo = new HashMap<>();
        for (Visita visita : visitaRepository.listarTodas()) {
            String codigo = visita.getCodigoInmueble();
            int cantidadActual = conteo.getOrDefault(codigo, 0);
            conteo.put(codigo, cantidadActual + 1);
        }
        return conteo;
    }

    /**
     * Recorre todas las visitas y cuenta cuántas hay por cada ID de cliente.
     * Usa HashMap para acumulación: O(n) en el número de visitas.
     */
    private Map<String, Integer> contarVisitasPorCliente() {
        Map<String, Integer> conteo = new HashMap<>();
        for (Visita visita : visitaRepository.listarTodas()) {
            String idCliente = visita.getIdCliente();
            int cantidadActual = conteo.getOrDefault(idCliente, 0);
            conteo.put(idCliente, cantidadActual + 1);
        }
        return conteo;
    }
}
