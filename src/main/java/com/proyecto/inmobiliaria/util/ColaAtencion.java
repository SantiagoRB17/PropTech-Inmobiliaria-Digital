package com.proyecto.inmobiliaria.util;

/**
 * Cola genérica (FIFO) implementada con lista enlazada simple.
 * Uso en el sistema: gestiona solicitudes de visita y consultas en estricto
 * orden de llegada — el primero en pedir atención es el primero en ser atendido.
 */
public class ColaAtencion<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> frente;
    private Nodo<T> ultimo;
    private int tamanio;


    public ColaAtencion() {
        this.frente = null;
        this.ultimo = null;
        this.tamanio = 0;
    }

    /**
     * Agrega un elemento al final de la cola. O(1)
     */
    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.siguiente = nuevo;
            ultimo = nuevo;
        }
        tamanio++;
    }

    /**
     * Elimina y retorna el primer elemento de la cola. O(1)
     */
    public T desencolar() {
        if (estaVacia()) {
            throw new RuntimeException("La cola de atención está vacía.");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            ultimo = null;
        }
        tamanio--;
        return dato;
    }

    /**
     * Consulta el primer elemento sin eliminarlo. O(1)
     */
    public T verFrente() {
        if (estaVacia()) {
            throw new RuntimeException("La cola de atención está vacía.");
        }
        return frente.dato;
    }


    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }
}
