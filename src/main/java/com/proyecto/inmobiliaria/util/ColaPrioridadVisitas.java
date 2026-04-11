package com.proyecto.inmobiliaria.util;

import com.proyecto.inmobiliaria.model.Visita;

import java.util.ArrayList;
import java.util.List;

/**
 * Cola de prioridad (Max-Heap) para visitas, implementada sobre un ArrayList.
 * La visita con mayor valor en el campo {@code prioridad} siempre sale primero.
 * El campo {@code prioridad} es calculado por el Service antes de encolar,
 * combinando los siguientes criterios del negocio:
 *   - Cliente VIP                          → peso alto
 *   - Visita marcada como urgente          → peso alto
 *   - Cliente con alta intención de cierre → peso medio
 *   - Inmueble con alta demanda            → peso medio
 *   - Alerta de contrato por vencer        → peso medio
 * Estructura interna: árbol binario completo representado en un ArrayList.
 *   - Padre de i    → (i - 1) / 2
 *   - Hijo izq de i → 2*i + 1
 *   - Hijo der de i → 2*i + 2
 */
public class ColaPrioridadVisitas {

    private final List<Visita> heap;

    public ColaPrioridadVisitas() {
        this.heap = new ArrayList<>();
    }


    /**
     * Inserta una visita en el heap manteniendo la propiedad max-heap. O(log n)
     * La visita debe tener su campo {@code prioridad} ya calculado.
     */
    public void insertar(Visita visita) {
        heap.add(visita);
        subirHeap(heap.size() - 1);
    }

    /**
     * Extrae y retorna la visita con mayor prioridad. O(log n)
     */
    public Visita extraerMaximo() {
        if (estaVacia()) {
            throw new RuntimeException("No hay visitas en la cola de prioridad.");
        }
        Visita maximo = heap.get(0);
        Visita ultimo = heap.remove(heap.size() - 1);
        if (!estaVacia()) {
            heap.set(0, ultimo);
            bajarHeap(0);
        }
        return maximo;
    }

    /**
     * Retorna la visita con mayor prioridad sin extraerla. O(1)
     */
    public Visita verMaximo() {
        if (estaVacia()) {
            throw new RuntimeException("No hay visitas en la cola de prioridad.");
        }
        return heap.get(0);
    }

    /**
     * Retorna todos los elementos del heap como lista (sin orden garantizado,
     * excepto que heap[0] es el máximo).
     */
    public List<Visita> obtenerTodos() {
        return new ArrayList<>(heap);
    }


    /**
     * Sube el elemento en posición {@code i} hasta que su padre sea mayor o
     * llegue a la raíz — restaura la propiedad max-heap tras una inserción.
     */
    private void subirHeap(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (heap.get(i).getPrioridad() > heap.get(padre).getPrioridad()) {
                intercambiar(i, padre);
                i = padre;
            } else {
                break;
            }
        }
    }

    /**
     * Baja el elemento en posición {@code i} hacia el hijo mayor hasta que
     * sea mayor que ambos hijos — restaura la propiedad max-heap tras una extracción.
     */
    private void bajarHeap(int i) {
        int n = heap.size();
        while (true) {
            int mayor = i;
            int hijoIzq = 2 * i + 1;
            int hijoDer = 2 * i + 2;

            if (hijoIzq < n && heap.get(hijoIzq).getPrioridad() > heap.get(mayor).getPrioridad()) {
                mayor = hijoIzq;
            }
            if (hijoDer < n && heap.get(hijoDer).getPrioridad() > heap.get(mayor).getPrioridad()) {
                mayor = hijoDer;
            }

            if (mayor != i) {
                intercambiar(i, mayor);
                i = mayor;
            } else {
                break;
            }
        }
    }

    private void intercambiar(int i, int j) {
        Visita temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }


    public boolean estaVacia() {
        return heap.isEmpty();
    }

    public int tamanio() {
        return heap.size();
    }
}
