package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.enums.TipoCliente;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private InmuebleRepository inmuebleRepository;
    @Mock private GrafoRepository grafoRepository;

    @InjectMocks private ClienteService clienteService;

    private Cliente clienteBase() {
        return Cliente.builder()
                .identificacion("CLI-001")
                .nombre("Juan Pérez")
                .tipoCliente(TipoCliente.COMPRADOR)
                .presupuesto(300_000)
                .build();
    }

    @Test
    void debeRechazarRegistroConIdDuplicado() {
        Cliente cliente = clienteBase();
        when(clienteRepository.existePorId("CLI-001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> clienteService.registrar(cliente));
    }

    @Test
    void debeRegistrarClienteEnGrafoAlCrear() {
        Cliente cliente = clienteBase();
        when(clienteRepository.existePorId("CLI-001")).thenReturn(false);

        clienteService.registrar(cliente);

        verify(grafoRepository).registrarCliente("CLI-001");
    }

    @Test
    void debeGenerarIdentificacionAutomaticaCuandoEsNula() {
        Cliente cliente = Cliente.builder().nombre("Ana López").tipoCliente(TipoCliente.VIP).build();
        when(clienteRepository.listarTodos()).thenReturn(new ArrayList<>());

        Cliente resultado = clienteService.registrar(cliente);

        assertNotNull(resultado.getIdentificacion());
        assertTrue(resultado.getIdentificacion().startsWith("CLI-"));
    }

    @Test
    void debeLanzarExcepcionAlBuscarClienteInexistente() {
        when(clienteRepository.buscarPorId("INEXISTENTE")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> clienteService.buscarPorId("INEXISTENTE"));
    }

    @Test
    void debeAgregarInmuebleAFavoritos() {
        Cliente cliente = clienteBase();
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);

        clienteService.agregarFavorito("CLI-001", "INM-001");

        assertTrue(cliente.getFavoritos().contains("INM-001"));
    }

    @Test
    void debeRechazarFavoritoConInmuebleInexistente() {
        Cliente cliente = clienteBase();
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.existePorId("INM-999")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> clienteService.agregarFavorito("CLI-001", "INM-999"));
    }

    @Test
    void descartarInmuebleLoEliminaDeFavoritosYLoAgregaADescartados() {
        Cliente cliente = clienteBase();
        cliente.getFavoritos().add("INM-001");
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);

        clienteService.descartarInmueble("CLI-001", "INM-001");

        assertFalse(cliente.getFavoritos().contains("INM-001"), "Debe quitarse de favoritos");
        assertTrue(cliente.getInmueblesDescartados().contains("INM-001"), "Debe quedar en descartados");
    }

    @Test
    void quitarFavoritoLoEliminaSoloDeEsaLista() {
        Cliente cliente = clienteBase();
        cliente.getFavoritos().add("INM-001");
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);

        clienteService.quitarFavorito("CLI-001", "INM-001");

        assertFalse(cliente.getFavoritos().contains("INM-001"));
    }

    @Test
    void agregarFavoritoDosVecesNoLoDuplica() {
        Cliente cliente = clienteBase();
        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.existePorId("INM-001")).thenReturn(true);

        clienteService.agregarFavorito("CLI-001", "INM-001");
        clienteService.agregarFavorito("CLI-001", "INM-001"); // segunda vez

        assertEquals(1, cliente.getFavoritos().size());
    }
}
