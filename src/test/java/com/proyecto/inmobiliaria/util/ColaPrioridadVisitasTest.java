package com.proyecto.inmobiliaria.util;

import com.proyecto.inmobiliaria.model.Visita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaPrioridadVisitasTest {

    private ColaPrioridadVisitas cola;

    @BeforeEach
    void setUp() {
        cola = new ColaPrioridadVisitas();
    }

    private Visita visita(String id, int prioridad) {
        return Visita.builder().idVisita(id).prioridad(prioridad).build();
    }

    @Test
    void heapVacioInicialmente() {
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamanio());
    }

    @Test
    void insertarAumentaTamanio() {
        cola.insertar(visita("VIS-001", 5));
        assertEquals(1, cola.tamanio());
        assertFalse(cola.estaVacia());
    }

    @Test
    void extraerMaximoRetornaSiempreLaDeMayorPrioridad() {
        cola.insertar(visita("VIS-001", 3));
        cola.insertar(visita("VIS-002", 18));
        cola.insertar(visita("VIS-003", 8));

        assertEquals("VIS-002", cola.extraerMaximo().getIdVisita());
    }

    @Test
    void extraerMaximoMantieneOrdenDecrecienteDePrioridad() {
        cola.insertar(visita("VIS-001", 5));
        cola.insertar(visita("VIS-002", 18));
        cola.insertar(visita("VIS-003", 3));
        cola.insertar(visita("VIS-004", 10));

        int primera = cola.extraerMaximo().getPrioridad();
        int segunda = cola.extraerMaximo().getPrioridad();
        int tercera = cola.extraerMaximo().getPrioridad();
        int cuarta  = cola.extraerMaximo().getPrioridad();

        assertTrue(primera >= segunda);
        assertTrue(segunda >= tercera);
        assertTrue(tercera >= cuarta);
    }

    @Test
    void verMaximoNoAlteraTamanio() {
        cola.insertar(visita("VIS-001", 10));
        cola.verMaximo();
        assertEquals(1, cola.tamanio());
    }

    @Test
    void verMaximoRetornaElDeMayorPrioridad() {
        cola.insertar(visita("VIS-001", 3));
        cola.insertar(visita("VIS-002", 15));
        assertEquals("VIS-002", cola.verMaximo().getIdVisita());
    }

    @Test
    void extraerMaximoEnHeapVacioLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> cola.extraerMaximo());
    }

    @Test
    void verMaximoEnHeapVacioLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> cola.verMaximo());
    }

    @Test
    void tamanioDisminuyeAlExtraer() {
        cola.insertar(visita("VIS-001", 5));
        cola.insertar(visita("VIS-002", 3));
        cola.extraerMaximo();
        assertEquals(1, cola.tamanio());
    }

    @Test
    void obtenerTodosRetornaCopiaSinModificarHeap() {
        cola.insertar(visita("VIS-001", 10));
        cola.insertar(visita("VIS-002", 5));
        int tamanioAntes = cola.tamanio();
        cola.obtenerTodos();
        assertEquals(tamanioAntes, cola.tamanio());
    }
}
