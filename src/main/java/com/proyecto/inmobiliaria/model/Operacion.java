package com.proyecto.inmobiliaria.model;

import com.proyecto.inmobiliaria.model.enums.EstadoOperacion;
import com.proyecto.inmobiliaria.model.enums.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Representa una operación comercial (venta o arriendo).
 * Se almacena en HashMap<String, Operacion> — búsqueda O(1) por idOperacion.
 * Los cambios de estado se rastrean con PilaAcciones (LIFO) para poder deshacer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operacion {

    private String idOperacion;


    private String codigoInmueble;

    private String idCliente;

    private String idAsesor;

    private LocalDate fecha;

    private TipoOperacion tipo;

    private double valorAcordado;

    private double comision;

    private EstadoOperacion estado;
}
