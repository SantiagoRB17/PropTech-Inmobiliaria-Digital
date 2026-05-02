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
    private static final int UMBRAL_CAMBIOS_PRECIO   = 2;
    private static final int UMBRAL_VISITAS_ZONA     = 3;

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
        alertasGeneradas.addAll(detectarCambiosDePrecioFrecuentes());
        alertasGeneradas.addAll(detectarConcentracionPorZona());
        return alertasGeneradas;
    }

    /**
     * Detecta inmuebles con muchas visitas que siguen disponibles.
     * Criterio: >= UMBRAL_VISITAS_INMUEBLE visitas y el inmueble todavía está disponible.
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
                    && inmuebleRepository.buscarPorId(codigo).isDisponible()) {

                if (!alertaService.existeAlertaActivaPara(TipoAlerta.INMUEBLE_SIN_CIERRE, codigo)) {
                    alertas.add(alertaService.generarAlerta(
                            TipoAlerta.INMUEBLE_SIN_CIERRE,
                            "Inmueble " + codigo + " acumula " + cantidad
                                    + " visitas sin cierre. Alta demanda sin conversión.",
                            NivelAtencion.MEDIA,
                            codigo
                    ));
                } else {
                    alertas.add(alertaService.buscarAlertaActivaPara(TipoAlerta.INMUEBLE_SIN_CIERRE, codigo));
                }
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

            List<String> negociados = cliente.getInmueblesNegociados();
            if (cantidad >= UMBRAL_VISITAS_CLIENTE
                    && (negociados == null || negociados.isEmpty())) {

                if (!alertaService.existeAlertaActivaPara(TipoAlerta.CLIENTE_SIN_SEGUIMIENTO, idCliente)) {
                    alertas.add(alertaService.generarAlerta(
                            TipoAlerta.CLIENTE_SIN_SEGUIMIENTO,
                            "Cliente " + cliente.getNombre() + " (" + idCliente + ") tiene "
                                    + cantidad + " visitas sin ninguna operación registrada.",
                            NivelAtencion.MEDIA,
                            idCliente
                    ));
                } else {
                    alertas.add(alertaService.buscarAlertaActivaPara(TipoAlerta.CLIENTE_SIN_SEGUIMIENTO, idCliente));
                }
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
            java.util.List<String> agendadas = asesor.getVisitasAgendadas();
            int cantidad = agendadas == null ? 0 : agendadas.size();
            if (cantidad >= UMBRAL_VISITAS_ASESOR) {
                String id = asesor.getIdentificacion();
                if (!alertaService.existeAlertaActivaPara(TipoAlerta.VISITA_PENDIENTE, id)) {
                    alertas.add(alertaService.generarAlerta(
                            TipoAlerta.VISITA_PENDIENTE,
                            "Asesor " + asesor.getNombre() + " (" + id
                                    + ") tiene " + cantidad + " visitas agendadas. Posible sobrecarga.",
                            NivelAtencion.ALTA,
                            id
                    ));
                } else {
                    alertas.add(alertaService.buscarAlertaActivaPara(TipoAlerta.VISITA_PENDIENTE, id));
                }
            }
        }
        return alertas;
    }

    /**
     * Detecta inmuebles cuyo precio ha cambiado >= UMBRAL_CAMBIOS_PRECIO veces.
     * Indica inestabilidad comercial: el propietario ajusta precio sin lograr cierre.
     */
    public List<Alerta> detectarCambiosDePrecioFrecuentes() {
        List<Alerta> alertas = new ArrayList<>();
        Map<String, Integer> cambios = inmuebleRepository.getCambiosDePrecio();

        for (Map.Entry<String, Integer> entrada : cambios.entrySet()) {
            String codigo  = entrada.getKey();
            int cantidad   = entrada.getValue();
            String entidad = "precio-" + codigo;

            if (cantidad >= UMBRAL_CAMBIOS_PRECIO) {
                if (!alertaService.existeAlertaActivaPara(TipoAlerta.INMUEBLE_SIN_CIERRE, entidad)) {
                    alertas.add(alertaService.generarAlerta(
                            TipoAlerta.INMUEBLE_SIN_CIERRE,
                            "El inmueble " + codigo + " ha cambiado de precio " + cantidad
                                    + " veces sin registrar cierre. Posible inestabilidad comercial.",
                            NivelAtencion.MEDIA,
                            entidad
                    ));
                } else {
                    alertas.add(alertaService.buscarAlertaActivaPara(TipoAlerta.INMUEBLE_SIN_CIERRE, entidad));
                }
            }
        }
        return alertas;
    }

    /**
     * Detecta ciudades con >= UMBRAL_VISITAS_ZONA visitas, indicando alta concentración
     * de interés en esa zona en el período actual.
     * Usa TipoAlerta.ALTA_DEMANDA ya que la causa es exceso de demanda geográfica.
     */
    public List<Alerta> detectarConcentracionPorZona() {
        List<Alerta> alertas = new ArrayList<>();
        Map<String, Integer> visitasPorCiudad = new HashMap<>();

        for (Visita visita : visitaRepository.listarTodas()) {
            if (inmuebleRepository.existePorId(visita.getCodigoInmueble())) {
                String ciudad = inmuebleRepository.buscarPorId(visita.getCodigoInmueble()).getCiudad();
                visitasPorCiudad.put(ciudad, visitasPorCiudad.getOrDefault(ciudad, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entrada : visitasPorCiudad.entrySet()) {
            String ciudad  = entrada.getKey();
            int cantidad   = entrada.getValue();
            String entidad = "zona-" + ciudad;

            if (cantidad >= UMBRAL_VISITAS_ZONA) {
                if (!alertaService.existeAlertaActivaPara(TipoAlerta.ALTA_DEMANDA, entidad)) {
                    alertas.add(alertaService.generarAlerta(
                            TipoAlerta.ALTA_DEMANDA,
                            "La zona " + ciudad + " concentra " + cantidad
                                    + " visitas en el período actual. Alta demanda detectada.",
                            NivelAtencion.ALTA,
                            entidad
                    ));
                } else {
                    alertas.add(alertaService.buscarAlertaActivaPara(TipoAlerta.ALTA_DEMANDA, entidad));
                }
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
