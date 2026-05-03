package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsesorService {

    private final AsesorRepository asesorRepository;
    private final InmuebleRepository inmuebleRepository;

    public Asesor registrar(Asesor asesor) {
        if (asesorRepository.existePorId(asesor.getIdentificacion())) {
            throw new RuntimeException("Ya existe un asesor con identificación: " + asesor.getIdentificacion());
        }
        if (asesor.getInmueblesAsignados() == null) asesor.setInmueblesAsignados(new ArrayList<>());
        if (asesor.getVisitasAgendadas() == null)   asesor.setVisitasAgendadas(new ArrayList<>());
        asesorRepository.guardar(asesor);
        return asesor;
    }

    public Asesor buscarPorId(String identificacion) {
        Asesor asesor = asesorRepository.buscarPorId(identificacion);
        if (asesor == null) {
            throw new RuntimeException("Asesor no encontrado: " + identificacion);
        }
        return asesor;
    }

    public List<Asesor> listarTodos() {
        return asesorRepository.listarTodos();
    }

    public Asesor actualizar(String identificacion, Asesor datos) {
        Asesor existente = buscarPorId(identificacion);
        existente.setNombre(datos.getNombre());
        existente.setContacto(datos.getContacto());
        existente.setEspecialidad(datos.getEspecialidad());
        existente.setZonaAsignada(datos.getZonaAsignada());
        asesorRepository.guardar(existente);
        return existente;
    }

    public void eliminar(String identificacion) {
        buscarPorId(identificacion);
        asesorRepository.eliminar(identificacion);
    }

    /**
     * Asigna un inmueble al portafolio del asesor.
     * Valida que tanto el asesor como el inmueble existan.
     */
    public void asignarInmueble(String identificacionAsesor, String codigoInmueble) {
        Asesor asesor = buscarPorId(identificacionAsesor);
        if (!inmuebleRepository.existePorId(codigoInmueble)) {
            throw new RuntimeException("Inmueble no encontrado: " + codigoInmueble);
        }
        if (!asesor.getInmueblesAsignados().contains(codigoInmueble)) {
            asesor.getInmueblesAsignados().add(codigoInmueble);
        }
    }

    /**
     * Registra un cierre de negocio exitoso para el asesor.
     * Incrementa su contador de cierres realizados.
     */
    public void registrarCierre(String identificacionAsesor) {
        Asesor asesor = buscarPorId(identificacionAsesor);
        asesor.setCierresRealizados(asesor.getCierresRealizados() + 1);
    }
}
