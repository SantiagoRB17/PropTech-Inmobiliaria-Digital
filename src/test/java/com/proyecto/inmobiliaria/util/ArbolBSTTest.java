package com.proyecto.inmobiliaria.util;

import com.proyecto.inmobiliaria.model.Inmueble;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArbolBSTTest {

    private ArbolBST arbol;

    @BeforeEach
    void setUp() {
        arbol = new ArbolBST();
    }

    private Inmueble inmueble(String codigo, double precio) {
        return Inmueble.builder().codigo(codigo).precio(precio).build();
    }

    @Test
    void estaVacioInicialmente() {
        assertTrue(arbol.estaVacio());
    }

    @Test
    void noEstaVacioDespuesDeInsertar() {
        arbol.insertar(inmueble("INM-001", 200_000));
        assertFalse(arbol.estaVacio());
    }

    @Test
    void inordenRetornaOrdenadosDeMenorAMayorPrecio() {
        arbol.insertar(inmueble("INM-003", 300_000));
        arbol.insertar(inmueble("INM-001", 100_000));
        arbol.insertar(inmueble("INM-002", 200_000));

        List<Inmueble> resultado = arbol.inorden();

        assertEquals(3, resultado.size());
        assertEquals(100_000, resultado.get(0).getPrecio());
        assertEquals(200_000, resultado.get(1).getPrecio());
        assertEquals(300_000, resultado.get(2).getPrecio());
    }

    @Test
    void buscarPorRangoRetornaInmueblesEnRango() {
        arbol.insertar(inmueble("INM-001", 100_000));
        arbol.insertar(inmueble("INM-002", 250_000));
        arbol.insertar(inmueble("INM-003", 400_000));
        arbol.insertar(inmueble("INM-004", 600_000));

        List<Inmueble> resultado = arbol.buscarPorRango(150_000, 500_000);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(i -> i.getPrecio() >= 150_000 && i.getPrecio() <= 500_000));
    }

    @Test
    void buscarPorRangoIncluyeLimitesExactos() {
        arbol.insertar(inmueble("INM-001", 100_000));
        arbol.insertar(inmueble("INM-002", 300_000));
        arbol.insertar(inmueble("INM-003", 500_000));

        List<Inmueble> resultado = arbol.buscarPorRango(100_000, 300_000);

        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorRangoEnArbolVacioRetornaListaVacia() {
        List<Inmueble> resultado = arbol.buscarPorRango(0, 1_000_000);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarPorRangoSinCoincidenciasRetornaListaVacia() {
        arbol.insertar(inmueble("INM-001", 500_000));

        List<Inmueble> resultado = arbol.buscarPorRango(100_000, 300_000);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminarNodoHojaLoQuitaDelArbol() {
        arbol.insertar(inmueble("INM-001", 200_000));
        arbol.insertar(inmueble("INM-002", 100_000)); // hoja izquierda

        arbol.eliminar("INM-002");

        List<Inmueble> inorden = arbol.inorden();
        assertEquals(1, inorden.size());
        assertEquals("INM-001", inorden.get(0).getCodigo());
    }

    @Test
    void eliminarNodoConDosHijosMantieneOrdenBST() {
        arbol.insertar(inmueble("INM-003", 300_000)); // raíz
        arbol.insertar(inmueble("INM-001", 100_000)); // hijo izquierdo
        arbol.insertar(inmueble("INM-005", 500_000)); // hijo derecho
        arbol.insertar(inmueble("INM-004", 400_000)); // sucesor inorden de la raíz

        arbol.eliminar("INM-003");

        List<Inmueble> inorden = arbol.inorden();
        assertEquals(3, inorden.size());
        for (int i = 0; i < inorden.size() - 1; i++) {
            assertTrue(inorden.get(i).getPrecio() <= inorden.get(i + 1).getPrecio(),
                    "El árbol debe seguir ordenado tras eliminar un nodo con dos hijos");
        }
    }

    @Test
    void eliminarNodoConUnHijoReasignaHijo() {
        arbol.insertar(inmueble("INM-001", 200_000));
        arbol.insertar(inmueble("INM-002", 300_000)); // hijo derecho de raíz

        arbol.eliminar("INM-001");

        List<Inmueble> inorden = arbol.inorden();
        assertEquals(1, inorden.size());
        assertEquals("INM-002", inorden.get(0).getCodigo());
    }
}
