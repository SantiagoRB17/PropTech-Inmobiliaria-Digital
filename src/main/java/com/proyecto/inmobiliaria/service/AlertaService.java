package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Alerta;
import com.proyecto.inmobiliaria.model.enums.NivelAtencion;
import com.proyecto.inmobiliaria.model.enums.TipoAlerta;
import com.proyecto.inmobiliaria.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service de alertas del sistema.
 */
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;

    /**
     * Crea una nueva alerta del sistema.
     *
     * @param tipo              categoría de la alerta
     * @param descripcion       mensaje descriptivo del problema detectado
     * @param nivel             severidad (BAJA, MEDIA, ALTA, CRITICA)
     * @param entidadRelacionada código o ID de la entidad que disparó la alerta
     * @return la alerta generada y guardada
     */
    public Alerta generarAlerta(TipoAlerta tipo, String descripcion,
                                NivelAtencion nivel, String entidadRelacionada) {
        Alerta alerta = Alerta.builder()
                .idAlerta(UUID.randomUUID().toString())
                .tipo(tipo)
                .descripcion(descripcion)
                .nivel(nivel)
                .fecha(LocalDateTime.now())
                .resuelta(false)
                .entidadRelacionada(entidadRelacionada)
                .build();

        alertaRepository.guardar(alerta);
        return alerta;
    }

    /**
     * Retorna true si ya existe una alerta activa (no resuelta) para ese tipo y entidad.
     */
    public boolean existeAlertaActivaPara(TipoAlerta tipo, String entidadRelacionada) {
        return alertaRepository.existeAlertaActivaPara(tipo, entidadRelacionada);
    }

    public Alerta buscarPorId(String idAlerta) {
        Alerta alerta = alertaRepository.buscarPorId(idAlerta);
        if (alerta == null) {
            throw new RuntimeException("Alerta no encontrada: " + idAlerta);
        }
        return alerta;
    }

    public void marcarResuelta(String idAlerta) {
        alertaRepository.marcarComoResuelta(idAlerta);
    }

    public List<Alerta> listarNoResueltas() {
        return alertaRepository.listarNoResueltas();
    }

    public List<Alerta> listarHistorial() {
        return alertaRepository.listarHistorial();
    }

    public List<Alerta> listarTodas() {
        return alertaRepository.listarTodas();
    }
}
