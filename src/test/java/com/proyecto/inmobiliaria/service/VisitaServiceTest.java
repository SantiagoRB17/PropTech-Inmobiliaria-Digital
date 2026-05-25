package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Asesor;
import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Visita;
import com.proyecto.inmobiliaria.model.enums.EstadoBusqueda;
import com.proyecto.inmobiliaria.model.enums.TipoCliente;
import com.proyecto.inmobiliaria.repository.AsesorRepository;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import com.proyecto.inmobiliaria.repository.VisitaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitaServiceTest {

    @Mock private VisitaRepository visitaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private InmuebleRepository inmuebleRepository;
    @Mock private AsesorRepository asesorRepository;
    @Mock private AlertaService alertaService;

    @InjectMocks private VisitaService visitaService;

    /** Visita con ID ya asignado para evitar que se llame listarTodas(). */
    private Visita visitaBase() {
        return Visita.builder()
                .idVisita("VIS-001")
                .codigoInmueble("INM-001")
                .idCliente("CLI-001")
                .idAsesor("ASE-001")
                .build();
    }

    private void configurarEntidadesValidas(Cliente cliente) {
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(asesorRepository.existePorId("ASE-001")).thenReturn(true);
        Asesor asesor = Asesor.builder().identificacion("ASE-001").build();
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);
    }

    @Test
    void prioridadClienteVIPActivo() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .tipoCliente(TipoCliente.VIP)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build();
        configurarEntidadesValidas(cliente);

        Visita resultado = visitaService.solicitarVisita(visitaBase());

        // base(3) + VIP(10) + ACTIVO(5) = 18
        assertEquals(18, resultado.getPrioridad());
    }

    @Test
    void prioridadClienteCompradorActivo() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .tipoCliente(TipoCliente.COMPRADOR)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build();
        configurarEntidadesValidas(cliente);

        Visita resultado = visitaService.solicitarVisita(visitaBase());

        // base(3) + ACTIVO(5) = 8
        assertEquals(8, resultado.getPrioridad());
    }

    @Test
    void prioridadClienteCompradorPausado() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .tipoCliente(TipoCliente.COMPRADOR)
                .estadoBusqueda(EstadoBusqueda.PAUSADO)
                .build();
        configurarEntidadesValidas(cliente);

        Visita resultado = visitaService.solicitarVisita(visitaBase());

        // solo base(3)
        assertEquals(3, resultado.getPrioridad());
    }

    @Test
    void debeRechazarVisitaConInmuebleInexistente() {
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> visitaService.solicitarVisita(visitaBase()));
    }

    @Test
    void debeRechazarVisitaConClienteInexistente() {
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> visitaService.solicitarVisita(visitaBase()));
    }

    @Test
    void debeRechazarVisitaConAsesorInexistente() {
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(
                Cliente.builder().identificacion("CLI-001").tipoCliente(TipoCliente.COMPRADOR).build());
        when(asesorRepository.existePorId("ASE-001")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> visitaService.solicitarVisita(visitaBase()));
    }

    @Test
    void debeEncarlarSolicitudAlSolicitar() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .tipoCliente(TipoCliente.COMPRADOR)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build();
        configurarEntidadesValidas(cliente);

        visitaService.solicitarVisita(visitaBase());

        verify(visitaRepository).encolarSolicitud(any(Visita.class));
    }

    @Test
    void debeAgregarVisitaAListaDeAsesorAlSolicitar() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .tipoCliente(TipoCliente.VIP)
                .estadoBusqueda(EstadoBusqueda.ACTIVO)
                .build();
        Asesor asesor = Asesor.builder().identificacion("ASE-001").build();
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(asesorRepository.existePorId("ASE-001")).thenReturn(true);
        when(asesorRepository.buscarPorId("ASE-001")).thenReturn(asesor);

        visitaService.solicitarVisita(visitaBase());

        assertTrue(asesor.getVisitasAgendadas().contains("VIS-001"));
    }
}
