package com.proyecto.inmobiliaria.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaAtencionTest {

    private ColaAtencion<String> cola;

    @BeforeEach
    void setUp() {
        cola = new ColaAtencion<>();
    }

    @Test
    void colaEstaVaciaInicialmente() {
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamanio());
    }

    @Test
    void encolarAumentaTamanio() {
        cola.encolar("A");
        assertEquals(1, cola.tamanio());
        assertFalse(cola.estaVacia());
    }

    @Test
    void desencolarRetornaEnOrdenFIFO() {
        cola.encolar("primero");
        cola.encolar("segundo");
        cola.encolar("tercero");

        assertEquals("primero", cola.desencolar());
        assertEquals("segundo", cola.desencolar());
        assertEquals("tercero", cola.desencolar());
    }

    @Test
    void verFrenteNoAlteraTamanio() {
        cola.encolar("dato");
        cola.verFrente();
        assertEquals(1, cola.tamanio());
    }

    @Test
    void verFrenteRetornaElPrimeroSinEliminar() {
        cola.encolar("primero");
        cola.encolar("segundo");
        assertEquals("primero", cola.verFrente());
        assertEquals(2, cola.tamanio());
    }

    @Test
    void alVaciarColaQuedaEnEstadoInicial() {
        cola.encolar("único");
        cola.desencolar();
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamanio());
    }

    @Test
    void desencolarEnColaVaciaLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> cola.desencolar());
    }

    @Test
    void verFrenteEnColaVaciaLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> cola.verFrente());
    }

    @Test
    void variosElementosMantienenOrdenFIFO() {
        for (int i = 1; i <= 5; i++) {
            cola.encolar("elemento-" + i);
        }
        for (int i = 1; i <= 5; i++) {
            assertEquals("elemento-" + i, cola.desencolar());
        }
    }
}
