package com.schooltracker.proyectofinal.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

//Esta clase modela el algoritmo de Dijkstra
public class Dijkstra {

    //estos dos datos se guardan en una cola de prioridad el vertice y la distancia en la que se le encontro
    private static class Nodo {
        Vertice vertice;
        double distancia;

        Nodo(Vertice vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }
    }

    public static Resultado calcular(Grafo grafo, int origenId, int destinoId) {

        //inicia validadno que existan ambos vertices
        Vertice origen = grafo.getVertice(origenId);
        Vertice destino = grafo.getVertice(destinoId);
        if (origen == null) {
            throw new IllegalArgumentException("El vértice de origen " + origenId + " no existe.");
        }
        if (destino == null) {
            throw new IllegalArgumentException("El vértice de destino " + destinoId + " no existe.");
        }

        //menor distancia conocida desde el origen
        Map<Vertice, Double> distancia = new HashMap<>();
        //de que vertice llega para con esto armar la ruta
        Map<Vertice, Vertice> anterior = new HashMap<>();
        //vertices ya visitados
        Set<Vertice> visitados = new HashSet<>();
        //en la cola saca primero el nodo con menor distancia
        PriorityQueue<Nodo> cola = new PriorityQueue<>((a, b) -> Double.compare(a.distancia, b.distancia));

        //comienza con origen distancia = 0
        distancia.put(origen, 0.0);
        cola.add(new Nodo(origen, 0.0));

        //repite para tomar el vertice mas cerquita que falta por visitar
        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();

            if (visitados.contains(actual.vertice)) {
                continue;
            }
            visitados.add(actual.vertice);

            if (actual.vertice.equals(destino)) {
                break;
            }

            //final revisa a cada vecinito
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
        //si el destino nunca se visito = no hay ruta
        if (!visitados.contains(destino)) {
            return new Resultado(origen, destino, new ArrayList<Vertice>(), 0, false);
        }

        //armado d la ruta de atras hacia adelante y volteada
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