package com.proyecto.inmobiliaria.model;

import com.proyecto.inmobiliaria.model.enums.EstadoBusqueda;
import com.proyecto.inmobiliaria.model.enums.TipoCliente;
import com.proyecto.inmobiliaria.model.enums.TipoInmueble;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un cliente de la plataforma.
 * Se almacena en HashMap<String, Cliente> — búsqueda O(1) por identificacion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private String identificacion;

    private String nombre;
    private String correo;
    private String telefono;

    private TipoCliente tipoCliente;

    private double presupuesto;

    @Builder.Default
    private List<String> zonasInteres = new ArrayList<>();

    private TipoInmueble tipoDeseado;

    private int habitacionesMin;

    private EstadoBusqueda estadoBusqueda;

    @Builder.Default
    private List<String> favoritos = new ArrayList<>();

    @Builder.Default
    private List<String> inmueblesConsultados = new ArrayList<>();

    @Builder.Default
    private List<String> inmueblesDescartados = new ArrayList<>();

    @Builder.Default
    private List<String> inmueblesNegociados = new ArrayList<>();
}
