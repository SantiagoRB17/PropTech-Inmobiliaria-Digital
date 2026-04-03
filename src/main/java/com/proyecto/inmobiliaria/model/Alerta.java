package com.proyecto.inmobiliaria.model;

import com.proyecto.inmobiliaria.model.enums.NivelAtencion;
import com.proyecto.inmobiliaria.model.enums.TipoAlerta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa una alerta generada por el sistema.
 * Las alertas son creadas por los Services cuando detectan condiciones
 * de negocio importantes (inmueble no disponible, presupuesto excedido, etc.).
 * Se almacenan en ArrayList para consulta cronológica O(n).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    private String idAlerta;

    private TipoAlerta tipo;

    private String descripcion;

    private NivelAtencion nivel;

    private LocalDateTime fecha;

    private boolean resuelta;

    private String entidadRelacionada;
}
