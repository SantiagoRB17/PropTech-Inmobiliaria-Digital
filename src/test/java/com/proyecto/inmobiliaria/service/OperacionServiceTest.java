package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.Operacion;
import com.proyecto.inmobiliaria.model.enums.EstadoOperacion;
import com.proyecto.inmobiliaria.model.enums.Finalidad;
import com.proyecto.inmobiliaria.model.enums.TipoOperacion;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.OperacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperacionServiceTest {

    @Mock private OperacionRepository operacionRepository;
    @Mock private InmuebleRepository inmuebleRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private AsesorRepository asesorRepository;

    @InjectMocks private OperacionService operacionService;

    private Inmueble inmuebleDisponibleVenta() {
        return Inmueble.builder()
                .codigo("INM-001")
                .disponible(true)
                .finalidad(Finalidad.VENTA)
                .build();
    }

    private Operacion operacionBase() {
        return Operacion.builder()
                .codigoInmueble("INM-001")
                .idCliente("CLI-001")
                .idAsesor("ASE-001")
                .tipo(TipoOperacion.VENTA)
                .valorAcordado(200_000)
                .build();
    }

    @Test
    void debeRechazarRegistroConInmuebleNoDisponible() {
        Inmueble noDisponible = Inmueble.builder()
                .codigo("INM-001").disponible(false).finalidad(Finalidad.VENTA).build();
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(noDisponible);

        assertThrows(RuntimeException.class, () -> operacionService.registrar(operacionBase()));
    }

    @Test
    void debeRechazarIncompatibilidadVentaSobreArriendo() {
        Inmueble arriendo = Inmueble.builder()
                .codigo("INM-001").disponible(true).finalidad(Finalidad.ARRIENDO).build();
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(arriendo);

        assertThrows(RuntimeException.class, () -> operacionService.registrar(operacionBase()));
    }

    @Test
    void debeRechazarIncompatibilidadArriendoSobreVenta() {
        Inmueble venta = Inmueble.builder()
                .codigo("INM-001").disponible(true).finalidad(Finalidad.VENTA).build();
        Operacion opArriendo = Operacion.builder()
                .codigoInmueble("INM-001").idCliente("CLI-001").idAsesor("ASE-001")
                .tipo(TipoOperacion.ARRIENDO).build();
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(venta);

        assertThrows(RuntimeException.class, () -> operacionService.registrar(opArriendo));
    }

    @Test
    void debeRegistrarOperacionValida() {
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(inmuebleDisponibleVenta());
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(
                Cliente.builder().identificacion("CLI-001").build());
        when(asesorRepository.existePorId("ASE-001")).thenReturn(true);
        when(operacionRepository.listarTodas()).thenReturn(new ArrayList<>());

        Operacion resultado = operacionService.registrar(operacionBase());

        assertEquals(EstadoOperacion.EN_PROCESO, resultado.getEstado());
        verify(operacionRepository).guardar(resultado);
    }

    @Test
    void cerrarOperacionMarcaInmuebleNoDisponible() {
        Inmueble inmueble = inmuebleDisponibleVenta();
        Asesor asesor = Asesor.builder().identificacion("ASE-001").cierresRealizados(0).build();
        Operacion operacion = Operacion.builder()
                .idOperacion("OP-001")
                .codigoInmueble("INM-001")
                .idAsesor("ASE-001")
                .estado(EstadoOperacion.EN_PROCESO)
                .build();

        when(operacionRepository.buscarPorId("OP-001")).thenReturn(operacion);
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(inmueble);
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);

        operacionService.cerrar("OP-001");

        assertFalse(inmueble.isDisponible(), "El inmueble debe quedar no disponible al cerrar");
    }

    @Test
    void cerrarOperacionIncrementaCierresDelAsesor() {
        Inmueble inmueble = inmuebleDisponibleVenta();
        Asesor asesor = Asesor.builder().identificacion("ASE-001").cierresRealizados(2).build();
        Operacion operacion = Operacion.builder()
                .idOperacion("OP-001")
                .codigoInmueble("INM-001")
                .idAsesor("ASE-001")
                .estado(EstadoOperacion.EN_PROCESO)
                .build();

        when(operacionRepository.buscarPorId("OP-001")).thenReturn(operacion);
        when(inmuebleRepository.buscarPorId("INM-001")).thenReturn(inmueble);
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);

        operacionService.cerrar("OP-001");

        assertEquals(3, asesor.getCierresRealizados());
        assertEquals(EstadoOperacion.CERRADA, operacion.getEstado());
    }

    @Test
    void cancelarOperacionCerradaLanzaExcepcion() {
        Operacion operacion = Operacion.builder()
                .idOperacion("OP-001")
                .estado(EstadoOperacion.CERRADA)
                .build();
        when(operacionRepository.buscarPorId("OP-001")).thenReturn(operacion);

        assertThrows(RuntimeException.class, () -> operacionService.cancelar("OP-001"));
    }

    @Test
    void cancelarOperacionEnProcesoLaCambiaDEstado() {
        Operacion operacion = Operacion.builder()
                .idOperacion("OP-001")
                .estado(EstadoOperacion.EN_PROCESO)
                .build();
        when(operacionRepository.buscarPorId("OP-001")).thenReturn(operacion);

        Operacion resultado = operacionService.cancelar("OP-001");

        assertEquals(EstadoOperacion.CANCELADA, resultado.getEstado());
    }
}
