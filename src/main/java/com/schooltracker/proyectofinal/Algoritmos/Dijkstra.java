package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.Vertice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Dijkstra: encuentra la ruta de menor costo entre dos vértices.
 * Los pesos no pueden ser negativos.
 * Complejidad: O((V + E) log V).
 */
public class Dijkstra {

    /** Un vértice junto con la distancia a la que se encontró. Se guarda en la cola de prioridad. */
    private static class Nodo {
        Vertice vertice;
        double distancia;

        Nodo(Vertice vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }
    }

    public static Resultado calcular(Grafo grafo, int origenId, int destinoId) {

        // 1. Validar que existan los dos vértices
        Vertice origen = grafo.getVertice(origenId);
        Vertice destino = grafo.getVertice(destinoId);
        if (origen == null) {
            throw new IllegalArgumentException("El vértice de origen " + origenId + " no existe.");
        }
        if (destino == null) {
            throw new IllegalArgumentException("El vértice de destino " + destinoId + " no existe.");
        }

        // 2. Preparar las estructuras
        Map<Vertice, Double> distancia = new HashMap<>();   // menor distancia conocida desde el origen
        Map<Vertice, Vertice> anterior = new HashMap<>();   // de qué vértice llegamos (para armar la ruta)
        Set<Vertice> visitados = new HashSet<>();           // vértices ya terminados
        // La cola saca primero el Nodo con menor distancia
        PriorityQueue<Nodo> cola = new PriorityQueue<>((a, b) -> Double.compare(a.distancia, b.distancia));

        // 3. Empezar en el origen con distancia 0
        distancia.put(origen, 0.0);
        cola.add(new Nodo(origen, 0.0));

        // 4. Repetir: tomar el vértice más cercano que falta por visitar
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            if (visitados.contains(actual.vertice)) {
                continue;   // ya se procesó con una distancia mejor
            }
            visitados.add(actual.vertice);

            if (actual.vertice.equals(destino)) {
                break;      // ya llegamos, no hace falta seguir
            }

            // 5. Revisar cada vecino: ¿pasar por el vértice actual sale más barato?
            for (Arista arista : grafo.getAdyacentes(actual.vertice.getId())) {
                if (arista.getPeso() < 0) {
                    throw new IllegalArgumentException("Dijkstra no permite pesos negativos.");
                }
                Vertice vecino = arista.getDestino();
                double nuevaDistancia = actual.distancia + arista.getPeso();
                boolean esMejor = !distancia.containsKey(vecino) || nuevaDistancia < distancia.get(vecino);

                if (!visitados.contains(vecino) && esMejor) {
                    distancia.put(vecino, nuevaDistancia);
                    anterior.put(vecino, actual.vertice);
                    cola.add(new Nodo(vecino, nuevaDistancia));
                }
            }
        }

        // 6. Si el destino nunca se visitó, no hay ruta
        if (!visitados.contains(destino)) {
            return new Resultado(origen, destino, new ArrayList<Vertice>(), 0, false);
        }

        // 7. Armar la ruta de atrás hacia adelante y voltearla
        List<Vertice> camino = new ArrayList<>();
        Vertice vertice = destino;
        while (vertice != null) {
            camino.add(vertice);
            vertice = anterior.get(vertice);
        }
        Collections.reverse(camino);

        return new Resultado(origen, destino, camino, distancia.get(destino), true);
    }
}
