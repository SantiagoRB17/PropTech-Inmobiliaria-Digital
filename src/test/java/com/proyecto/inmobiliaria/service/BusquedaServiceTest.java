package com.proyecto.inmobiliaria.service;

import com.proyecto.inmobiliaria.model.Cliente;
import com.proyecto.inmobiliaria.model.Inmueble;
import com.proyecto.inmobiliaria.model.enums.TipoInmueble;
import com.proyecto.inmobiliaria.repository.ClienteRepository;
import com.proyecto.inmobiliaria.repository.InmuebleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusquedaServiceTest {

    @Mock private InmuebleRepository inmuebleRepository;
    @Mock private ClienteRepository clienteRepository;

    @InjectMocks private BusquedaService busquedaService;

    @Test
    void debeLanzarExcepcionCuandoPrecioMinMayorQueMax() {
        assertThrows(RuntimeException.class,
                () -> busquedaService.buscarPorRangoPrecio(500_000, 100_000));
    }

    @Test
    void buscarPorRangoValidoDelegaAlRepositorio() {
        when(inmuebleRepository.buscarPorRangoPrecio(100_000, 300_000)).thenReturn(List.of());

        busquedaService.buscarPorRangoPrecio(100_000, 300_000);

        verify(inmuebleRepository).buscarPorRangoPrecio(100_000, 300_000);
    }

    @Test
    void buscarCompatiblesLanzaExcepcionConClienteInexistente() {
        when(clienteRepository.buscarPorId("INEXISTENTE")).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> busquedaService.buscarCompatiblesConCliente("INEXISTENTE"));
    }

    @Test
    void buscarCompatiblesFiltrarPorPresupuesto() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .presupuesto(200_000)
                .habitacionesMin(0)
                .build();
        Inmueble dentroDePrecio = Inmueble.builder()
                .codigo("INM-001").precio(150_000).disponible(true).habitaciones(2).build();
        Inmueble fueraDePrecio = Inmueble.builder()
                .codigo("INM-002").precio(350_000).disponible(true).habitaciones(2).build();

        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.listarTodos()).thenReturn(List.of(dentroDePrecio, fueraDePrecio));

        List<Inmueble> resultado = busquedaService.buscarCompatiblesConCliente("CLI-001");

        assertEquals(1, resultado.size());
        assertEquals("INM-001", resultado.get(0).getCodigo());
    }

    @Test
    void buscarCompatiblesFiltrarPorHabitacionesMinimas() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .presupuesto(1_000_000)
                .habitacionesMin(3)
                .build();
        Inmueble suficienteHabitaciones = Inmueble.builder()
                .codigo("INM-001").precio(100_000).disponible(true).habitaciones(4).build();
        Inmueble pocasHabitaciones = Inmueble.builder()
                .codigo("INM-002").precio(100_000).disponible(true).habitaciones(1).build();

        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.listarTodos()).thenReturn(List.of(suficienteHabitaciones, pocasHabitaciones));

        List<Inmueble> resultado = busquedaService.buscarCompatiblesConCliente("CLI-001");

        assertEquals(1, resultado.size());
        assertEquals("INM-001", resultado.get(0).getCodigo());
    }

    @Test
    void buscarCompatiblesExcluyeInmueblesNoDisponibles() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .presupuesto(500_000)
                .habitacionesMin(0)
                .build();
        Inmueble disponible = Inmueble.builder()
                .codigo("INM-001").precio(100_000).disponible(true).habitaciones(2).build();
        Inmueble noDisponible = Inmueble.builder()
                .codigo("INM-002").precio(100_000).disponible(false).habitaciones(2).build();

        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.listarTodos()).thenReturn(List.of(disponible, noDisponible));

        List<Inmueble> resultado = busquedaService.buscarCompatiblesConCliente("CLI-001");

        assertEquals(1, resultado.size());
        assertEquals("INM-001", resultado.get(0).getCodigo());
    }

    @Test
    void buscarCompatiblesFiltrarPorTipoDeseado() {
        Cliente cliente = Cliente.builder()
                .identificacion("CLI-001")
                .presupuesto(500_000)
                .habitacionesMin(0)
                .tipoDeseado(TipoInmueble.APARTAMENTO)
                .build();
        Inmueble apartamento = Inmueble.builder()
                .codigo("INM-001").precio(100_000).disponible(true).habitaciones(2)
                .tipo(TipoInmueble.APARTAMENTO).build();
        Inmueble casa = Inmueble.builder()
                .codigo("INM-002").precio(100_000).disponible(true).habitaciones(2)
                .tipo(TipoInmueble.CASA).build();

        when(clienteRepository.buscarPorId("CLI-001")).thenReturn(cliente);
        when(inmuebleRepository.listarTodos()).thenReturn(List.of(apartamento, casa));

        List<Inmueble> resultado = busquedaService.buscarCompatiblesConCliente("CLI-001");

        assertEquals(1, resultado.size());
        assertEquals("INM-001", resultado.get(0).getCodigo());
    }
}
