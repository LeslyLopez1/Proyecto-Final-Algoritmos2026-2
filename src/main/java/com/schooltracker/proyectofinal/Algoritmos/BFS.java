package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.Vertice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Búsqueda en anchura (BFS): visita primero los vecinos más cercanos al vértice inicial. */
public class BFS {

    /**
     * Regresa los vértices en el orden en que se visitan, empezando en inicioId.
     * Solo se visitan los vértices alcanzables desde el inicial.
     * Complejidad: O(V + E).
     */
    public static List<Vertice> recorrer(Grafo grafo, int inicioId) {
        Vertice inicio = grafo.getVertice(inicioId);
        if (inicio == null) {
            throw new IllegalArgumentException("El vértice inicial " + inicioId + " no existe.");
        }

        List<Vertice> orden = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Queue<Vertice> cola = new LinkedList<>();

        visitados.add(inicio.getId());
        cola.add(inicio);

        while (!cola.isEmpty()) {
            Vertice actual = cola.poll();
            orden.add(actual);

            for (Arista arista : grafo.getAdyacentes(actual.getId())) {
                Vertice vecino = arista.getDestino();
                if (!visitados.contains(vecino.getId())) {
                    visitados.add(vecino.getId());   // se marca al encolar para no repetirlo
                    cola.add(vecino);
                }
            }
        }
        return orden;
    }
}
