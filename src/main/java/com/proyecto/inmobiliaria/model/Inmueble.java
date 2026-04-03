package com.proyecto.inmobiliaria.model;

import com.proyecto.inmobiliaria.model.enums.EstadoInmueble;
import com.proyecto.inmobiliaria.model.enums.Finalidad;
import com.proyecto.inmobiliaria.model.enums.TipoInmueble;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un inmueble en la plataforma.
 * Se almacena en HashMap<String, Inmueble> — búsqueda O(1) por codigo.
 * También se indexa en ArbolBST por precio para búsquedas por rango O(log n).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inmueble {
    private String codigo;

    private String direccion;
    private String ciudad;
    private String barrio;

    private TipoInmueble tipo;
    private Finalidad finalidad;

    private double precio;

    private double area;

    private int habitaciones;
    private int banos;

    private EstadoInmueble estado;

    private boolean disponible;

    private String codigoAsesor;
}
