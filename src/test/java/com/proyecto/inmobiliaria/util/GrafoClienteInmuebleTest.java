package com.proyecto.inmobiliaria.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrafoClienteInmuebleTest {

    private GrafoClienteInmueble grafo;

    @BeforeEach
    void setUp() {
        grafo = new GrafoClienteInmueble();
    }

    @Test
    void agregarNodoCreaElNodo() {
        grafo.agregarNodo("CLI-001");
        assertTrue(grafo.existeNodo("CLI-001"));
    }

    @Test
    void agregarAristaCreaAmbosNodos() {
        grafo.agregarArista("CLI-001", "INM-001");
        assertTrue(grafo.existeNodo("CLI-001"));
        assertTrue(grafo.existeNodo("INM-001"));
    }

    @Test
    void aristaEsSimetrica() {
        grafo.agregarArista("CLI-001", "INM-001");

        assertTrue(grafo.existeRelacion("CLI-001", "INM-001"));
        // La arista debe existir en ambas direcciones
        List<String> vecinosInmueble = grafo.obtenerVecinos("INM-001");
        assertTrue(vecinosInmueble.contains("CLI-001"));
    }

    @Test
    void existeRelacionFalseSiNoHayArista() {
        grafo.agregarNodo("CLI-001");
        grafo.agregarNodo("INM-999");

        assertFalse(grafo.existeRelacion("CLI-001", "INM-999"));
    }

    @Test
    void totalAristasContaCorrectamenteSinDuplicar() {
        grafo.agregarArista("CLI-001", "INM-001");
        grafo.agregarArista("CLI-001", "INM-002");

        // 2 aristas, no 4 (no cuenta cada sentido como arista separada)
        assertEquals(2, grafo.totalAristas());
    }

    @Test
    void recomendarParaClienteSinNodoRetornaListaVacia() {
        List<String> recomendaciones = grafo.recomendarInmuebles("CLI-INEXISTENTE", 5);
        assertTrue(recomendaciones.isEmpty());
    }

    @Test
    void recomendarExcluyeInmueblesYaConocidosPorElCliente() {
        // CLI-001 ya interactuó con INM-001
        grafo.agregarArista("CLI-001", "INM-001");
        // CLI-002 interactuó con INM-001 y con INM-002
        grafo.agregarArista("CLI-002", "INM-001");
        grafo.agregarArista("CLI-002", "INM-002");

        // A 2 saltos desde CLI-001: INM-002 es candidato (vía CLI-002), INM-001 ya es conocido
        List<String> recomendaciones = grafo.recomendarInmuebles("CLI-001", 5);

        assertFalse(recomendaciones.contains("INM-001"), "No debe recomendar inmuebles ya conocidos");
        assertTrue(recomendaciones.contains("INM-002"), "Debe recomendar INM-002 descubierto a 2 saltos");
    }

    @Test
    void recomendarRespetaLimiteMaximo() {
        // CLI-001 → INM-001 → CLI-002, CLI-003, CLI-004 → INM-002, INM-003, INM-004
        grafo.agregarArista("CLI-001", "INM-001");
        grafo.agregarArista("CLI-002", "INM-001");
        grafo.agregarArista("CLI-002", "INM-002");
        grafo.agregarArista("CLI-003", "INM-001");
        grafo.agregarArista("CLI-003", "INM-003");
        grafo.agregarArista("CLI-004", "INM-001");
        grafo.agregarArista("CLI-004", "INM-004");

        List<String> recomendaciones = grafo.recomendarInmuebles("CLI-001", 2);

        assertTrue(recomendaciones.size() <= 2);
    }

    @Test
    void bfsNoCausaBucleInfinitoEnGrafosConCiclos() {
        // Grafo con ciclo: CLI-001 - INM-001 - CLI-002 - INM-002 - CLI-001 (ciclo indirecto)
        grafo.agregarArista("CLI-001", "INM-001");
        grafo.agregarArista("CLI-002", "INM-001");
        grafo.agregarArista("CLI-002", "INM-002");
        grafo.agregarArista("CLI-001", "INM-002"); // crea ciclo — CLI-001 ya conoce INM-002

        assertDoesNotThrow(() -> grafo.recomendarInmuebles("CLI-001", 5),
                "El BFS debe terminar sin bucle infinito aunque haya ciclos");
    }

    @Test
    void totalNodosContaCorrectamente() {
        grafo.agregarArista("CLI-001", "INM-001");
        grafo.agregarArista("CLI-001", "INM-002");

        assertEquals(3, grafo.totalNodos()); // CLI-001, INM-001, INM-002
    }
}
