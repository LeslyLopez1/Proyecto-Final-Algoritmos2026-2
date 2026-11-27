package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.Vertice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/** Búsqueda en profundidad (DFS): avanza por un camino hasta no poder más y luego regresa. */
public class DFS {

    /**
     * Regresa los vértices en el orden en que se visitan, empezando en inicioId.
     * Usa una pila en lugar de recursión para que no se desborde con grafos grandes.
     * Complejidad: O(V + E).
     */
    public static List<Vertice> recorrer(Grafo grafo, int inicioId) {
        Vertice inicio = grafo.getVertice(inicioId);
        if (inicio == null) {
            throw new IllegalArgumentException("El vértice inicial " + inicioId + " no existe.");
        }

        List<Vertice> orden = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Stack<Vertice> pila = new Stack<>();

        pila.push(inicio);
        while (!pila.isEmpty()) {
            Vertice actual = pila.pop();
            if (visitados.contains(actual.getId())) {
                continue;   // ya se visitó por otro camino
            }
            visitados.add(actual.getId());
            orden.add(actual);

            // Los vecinos se apilan al revés para que el primero de la lista sea el primero en visitarse
            List<Arista> vecinos = grafo.getAdyacentes(actual.getId());
            for (int i = vecinos.size() - 1; i >= 0; i--) {
                Vertice vecino = vecinos.get(i).getDestino();
                if (!visitados.contains(vecino.getId())) {
                    pila.push(vecino);
                }
            }
        }
        return orden;
    }
}
