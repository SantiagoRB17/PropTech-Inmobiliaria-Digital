package com.proyecto.inmobiliaria.util;

import com.proyecto.inmobiliaria.model.Inmueble;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol Binario de Búsqueda (BST) para inmuebles, ordenado por precio.
 * Permite buscar inmuebles dentro de un rango de precios
 * de forma eficiente, aprovechando la propiedad del BST para podar ramas
 * que no pueden contener resultados válidos.
 * En caso de precios iguales, el nuevo nodo se inserta a la derecha.
 */
public class ArbolBST {

    private static class Nodo {
        Inmueble inmueble;
        Nodo izquierdo;
        Nodo derecho;

        Nodo(Inmueble inmueble) {
            this.inmueble = inmueble;
            this.izquierdo = null;
            this.derecho = null;
        }
    }


    private Nodo raiz;


    public ArbolBST() {
        this.raiz = null;
    }


    /**
     * Inserta un inmueble en el árbol usando su precio como clave. O(log n)
     */
    public void insertar(Inmueble inmueble) {
        raiz = insertarRecursivo(raiz, inmueble);
    }

    private Nodo insertarRecursivo(Nodo nodo, Inmueble inmueble) {
        if (nodo == null) return new Nodo(inmueble);
        if (inmueble.getPrecio() < nodo.inmueble.getPrecio()) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, inmueble);
        } else {
            // Precio mayor o igual va a la derecha
            nodo.derecho = insertarRecursivo(nodo.derecho, inmueble);
        }
        return nodo;
    }

    /**
     * Retorna todos los inmuebles cuyo precio esté en [precioMin, precioMax]. O(log n + k)
     * La poda funciona así:
     *   - Si nodo.precio > precioMin → puede haber candidatos a la izquierda
     *   - Si nodo.precio < precioMax → puede haber candidatos a la derecha
     *   - Si nodo.precio está en rango → se incluye en resultados
     */
    public List<Inmueble> buscarPorRango(double precioMin, double precioMax) {
        List<Inmueble> resultados = new ArrayList<>();
        buscarRangoRecursivo(raiz, precioMin, precioMax, resultados);
        return resultados;
    }

    private void buscarRangoRecursivo(Nodo nodo, double min, double max, List<Inmueble> resultados) {
        if (nodo == null) return;

        // Podar rama izquierda: solo explorar si puede haber valores >= min
        if (nodo.inmueble.getPrecio() > min) {
            buscarRangoRecursivo(nodo.izquierdo, min, max, resultados);
        }

        // Incluir nodo actual si está dentro del rango
        if (nodo.inmueble.getPrecio() >= min && nodo.inmueble.getPrecio() <= max) {
            resultados.add(nodo.inmueble);
        }

        // Podar rama derecha: solo explorar si puede haber valores <= max
        if (nodo.inmueble.getPrecio() < max) {
            buscarRangoRecursivo(nodo.derecho, min, max, resultados);
        }
    }

    /**
     * Elimina un inmueble del árbol buscándolo por su código. O(log n)
     * Casos de eliminación:
     *   1. Nodo hoja → simplemente se elimina
     *   2. Un solo hijo → el hijo sube al lugar del nodo
     *   3. Dos hijos → se reemplaza con el sucesor inorden (mínimo del subárbol derecho)
     */
    public void eliminar(String codigoInmueble) {
        raiz = eliminarRecursivo(raiz, codigoInmueble);
    }

    private Nodo eliminarRecursivo(Nodo nodo, String codigo) {
        if (nodo == null) return null;

        if (nodo.inmueble.getCodigo().equals(codigo)) {
            // Caso 1: nodo hoja
            if (nodo.izquierdo == null && nodo.derecho == null) return null;
            // Caso 2: un solo hijo
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho == null) return nodo.izquierdo;
            // Caso 3: dos hijos — sucesor inorden
            Nodo sucesor = minimoNodo(nodo.derecho);
            nodo.inmueble = sucesor.inmueble;
            nodo.derecho = eliminarRecursivo(nodo.derecho, sucesor.inmueble.getCodigo());
        } else {
            // El código buscado puede estar en cualquier subárbol
            nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, codigo);
            nodo.derecho = eliminarRecursivo(nodo.derecho, codigo);
        }
        return nodo;
    }

    /** Encuentra el nodo con menor precio en el subárbol dado. */
    private Nodo minimoNodo(Nodo nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

    /**
     * Recorrido inorden: retorna inmuebles ordenados de menor a mayor precio. O(n)
     */
    public List<Inmueble> inorden() {
        List<Inmueble> resultado = new ArrayList<>();
        inordenRecursivo(raiz, resultado);
        return resultado;
    }

    private void inordenRecursivo(Nodo nodo, List<Inmueble> resultado) {
        if (nodo == null) return;
        inordenRecursivo(nodo.izquierdo, resultado);
        resultado.add(nodo.inmueble);
        inordenRecursivo(nodo.derecho, resultado);
    }

    public boolean estaVacio() {
        return raiz == null;
    }
}
