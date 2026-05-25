package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.GrafoRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InmuebleServiceTest {

    @Mock private InmuebleRepository inmuebleRepository;
    @Mock private AsesorRepository asesorRepository;
    @Mock private GrafoRepository grafoRepository;

    @InjectMocks private InmuebleService inmuebleService;

    private Inmueble inmuebleBase() {
        return Inmueble.builder()
                .codigo("INM-001")
                .precio(200_000)
                .disponible(true)
                .codigoAsesor("ASE-001")
                .build();
    }

    @Test
    void debeRechazarRegistroConAsesorInexistente() {
        Inmueble inmueble = inmuebleBase();
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(false);
        when(asesorRepository.existePorId("ASE-001")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> inmuebleService.registrar(inmueble));
    }

    @Test
    void debeRechazarRegistroConCodigoDuplicado() {
        Inmueble inmueble = inmuebleBase();
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> inmuebleService.registrar(inmueble));
    }

    @Test
    void debeGenerarCodigoAutomaticoCuandoEsNulo() {
        Inmueble inmueble = Inmueble.builder().precio(100_000).codigoAsesor("ASE-001").build();
        when(inmuebleRepository.listarTodos()).thenReturn(new ArrayList<>());
        when(asesorRepository.existePorId("ASE-001")).thenReturn(true);
        Asesor asesor = Asesor.builder().identificacion("ASE-001").build();
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);

        Inmueble resultado = inmuebleService.registrar(inmueble);

        assertNotNull(resultado.getCodigo());
        assertTrue(resultado.getCodigo().startsWith("INM-"));
    }

    @Test
    void debeRegistrarInmuebleEnGrafoAlCrear() {
        Inmueble inmueble = inmuebleBase();
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(false);
        when(asesorRepository.existePorId("ASE-001")).thenReturn(true);
        Asesor asesor = Asesor.builder().identificacion("ASE-001").build();
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);

        inmuebleService.registrar(inmueble);

        verify(grafoRepository).registrarInmueble("INM-001");
    }

    @Test
    void debeAgregarCodigoAListaDeAsesorAlRegistrar() {
        Inmueble inmueble = inmuebleBase();
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(false);
        when(asesorRepository.existePorId("ASE-001")).thenReturn(true);
        Asesor asesor = Asesor.builder().identificacion("ASE-001").build();
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);

        inmuebleService.registrar(inmueble);

        assertTrue(asesor.getInmueblesAsignados().contains("INM-001"));
    }

    @Test
    void debeLanzarExcepcionAlBuscarIdInexistente() {
        when(inmuebleRepository.buscarPorId("INEXISTENTE")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> inmuebleService.buscarPorId("INEXISTENTE"));
    }

    @Test
    void debeGuardarEstadoPrevioAlActualizar() {
        Inmueble existente = inmuebleBase();
        Inmueble datos = Inmueble.builder().precio(300_000).build();
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(existente);

        inmuebleService.actualizar("INM-001", datos);

        verify(inmuebleRepository).guardarEstadoPrevio(existente);
    }

    @Test
    void debeRegistrarCambioDePrecioCuandoPrecioModificado() {
        Inmueble existente = Inmueble.builder().codigo("INM-001").precio(200_000).build();
        Inmueble datos = Inmueble.builder().precio(350_000).build();
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(existente);

        inmuebleService.actualizar("INM-001", datos);

        verify(inmuebleRepository).registrarCambioPrecio("INM-001");
    }

    @Test
    void noDebeRegistrarCambioDePrecioCuandoPrecioEsIgual() {
        Inmueble existente = Inmueble.builder().codigo("INM-001").precio(200_000).build();
        Inmueble datos = Inmueble.builder().precio(200_000).build();
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(existente);

        inmuebleService.actualizar("INM-001", datos);

        verify(inmuebleRepository, never()).registrarCambioPrecio(any());
    }

    @Test
    void debeLanzarExcepcionAlDeshacerSinHistorial() {
        when(inmuebleRepository.hayAccionesPorDeshacer()).thenReturn(false);

        assertThrows(RuntimeException.class, () -> inmuebleService.deshacerUltimoCambio());
    }

    @Test
    void debeLanzarExcepcionCuandoPrecioMinMayorQueMax() {
        assertThrows(RuntimeException.class,
                () -> inmuebleService.buscarPorRangoPrecio(500_000, 100_000));
    }
}
