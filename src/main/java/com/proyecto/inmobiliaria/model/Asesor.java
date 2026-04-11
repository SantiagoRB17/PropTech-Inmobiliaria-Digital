package com.proyecto.inmobiliaria.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un asesor inmobiliario.
 * Se almacena en HashMap<String, Asesor> — búsqueda O(1) por identificacion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asesor {

    private String identificacion;

    private String nombre;

    private String contacto;

    private String especialidad;

    private String zonaAsignada;

    private int cierresRealizados;


    @Builder.Default
    private List<String> inmueblesAsignados = new ArrayList<>();

    @Builder.Default
    private List<String> visitasAgendadas = new ArrayList<>();
}
