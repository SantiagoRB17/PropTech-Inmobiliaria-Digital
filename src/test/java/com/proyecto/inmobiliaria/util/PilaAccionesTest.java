package com.proyecto.inmobiliaria.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PilaAccionesTest {

    private PilaAcciones<String> pila;

    @BeforeEach
    void setUp() {
        pila = new PilaAcciones<>();
    }

    @Test
    void pilaEstaVaciaInicialmente() {
        assertTrue(pila.estaVacia());
        assertEquals(0, pila.tamanio());
    }

    @Test
    void pushAumentaTamanio() {
        pila.push("A");
        assertEquals(1, pila.tamanio());
        assertFalse(pila.estaVacia());
    }

    @Test
    void popRetornaEnOrdenLIFO() {
        pila.push("primero");
        pila.push("segundo");
        pila.push("tercero");

        assertEquals("tercero", pila.pop());
        assertEquals("segundo", pila.pop());
        assertEquals("primero", pila.pop());
    }

    @Test
    void peekRetornaTopesinDesapilar() {
        pila.push("dato");
        assertEquals("dato", pila.peek());
        assertEquals(1, pila.tamanio()); // tamaño no cambia
    }

    @Test
    void popReduceTamanio() {
        pila.push("A");
        pila.push("B");
        pila.pop();
        assertEquals(1, pila.tamanio());
    }

    @Test
    void popEnPilaVaciaLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> pila.pop());
    }

    @Test
    void peekEnPilaVaciaLanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> pila.peek());
    }

    @Test
    void pilaQuedaVaciaDespuesDeDesapilarTodo() {
        pila.push("X");
        pila.pop();
        assertTrue(pila.estaVacia());
        assertEquals(0, pila.tamanio());
    }
}
