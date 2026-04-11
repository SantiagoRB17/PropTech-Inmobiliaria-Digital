package com.proyecto.inmobiliaria.util;

/**
 * Pila genérica (LIFO) implementada con lista enlazada simple.
 * Guarda snapshots del estado anterior de un Inmueble
 * antes de modificarlo, permitiendo deshacer el último cambio.
 */
public class PilaAcciones<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> tope;
    private int tamanio;


    public PilaAcciones() {
        this.tope = null;
        this.tamanio = 0;
    }


    /**
     * Apila un elemento en el tope. O(1)
     */
    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = tope;
        tope = nuevo;
        tamanio++;
    }

    /**
     * Desapila y retorna el elemento en el tope. O(1)
     */
    public T pop() {
        if (estaVacia()) {
            throw new RuntimeException("La pila está vacía — no hay acciones que deshacer.");
        }
        T dato = tope.dato;
        tope = tope.siguiente;
        tamanio--;
        return dato;
    }

    /**
     * Consulta el tope sin desapilar. O(1)
     */
    public T peek() {
        if (estaVacia()) {
            throw new RuntimeException("La pila está vacía.");
        }
        return tope.dato;
    }


    public boolean estaVacia() {
        return tope == null;
    }

    public int tamanio() {
        return tamanio;
    }
}
