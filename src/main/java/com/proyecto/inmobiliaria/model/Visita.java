package com.proyecto.inmobiliaria.model;

import com.proyecto.inmobiliaria.model.enums.EstadoVisita;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa una visita agendada a un inmueble.
 * Las visitas se gestionan con dos estructuras:
 * - ColaAtencion (FIFO) → para procesar solicitudes de visita en orden de llegada.
 * - ColaPrioridadVisitas  → para despachar visitas priorizando clientes VIP.
 * El campo 'prioridad' alimenta el heap: clientes VIP reciben valor más alto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visita {

    private String idVisita;

    private String codigoInmueble;

    private String idCliente;

    private String idAsesor;

    private LocalDate fecha;
    private LocalTime hora;

    private EstadoVisita estado;

    private String observaciones;

    private Integer prioridad;
}
