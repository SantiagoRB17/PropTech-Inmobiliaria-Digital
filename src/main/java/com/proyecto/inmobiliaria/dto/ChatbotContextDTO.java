package com.proyecto.inmobiliaria.dto;

import java.util.List;

/**
 * Snapshot del estado del sistema en memoria.
 * n8n consulta GET /api/v1/chatbot/context para obtener este objeto
 * y usarlo como contexto antes de enviarle la pregunta a Gemini.
 */
public record ChatbotContextDTO(
        int totalInmuebles,
        List<InmuebleResumen> inmueblesDisponibles,
        int totalClientes,
        List<ClienteResumen> clientes,
        List<AsesorResumen> asesores,
        List<VisitaResumen> visitasPendientes,
        List<VisitaResumen> visitasPrioritarias,
        int totalAlertas,
        List<AlertaResumen> alertasActivas,
        RangoPrecio resumenRangoPrecios,
        String generadoEn
) {

    /** Campos clave de un inmueble disponible. */
    public record InmuebleResumen(
            String codigo,
            String tipo,
            String finalidad,
            String zona,
            double precio,
            int habitaciones,
            String codigoAsesor,
            String nombreAsesor
    ) {}

    /** Campos clave de un asesor. */
    public record AsesorResumen(
            String codigo,
            String nombre,
            String contacto,
            String especialidad,
            String zonaAsignada,
            int cierresRealizados
    ) {}

    /** Campos clave de un cliente. */
    public record ClienteResumen(
            String id,
            String nombre,
            double presupuesto,
            String tipoInmuebleBuscado,
            List<String> zonasDeInteres
    ) {}

    /** Resumen de una visita pendiente. */
    public record VisitaResumen(
            String idVisita,
            String codigoInmueble,
            String idCliente,
            String estado
    ) {}

    /** Resumen de una alerta activa (no resuelta). */
    public record AlertaResumen(
            String idAlerta,
            String tipo,
            String descripcion,
            String nivel
    ) {}

    /** Rango de precios extraído del ArbolBST (inorden). */
    public record RangoPrecio(double precioMin, double precioMax) {}
}
