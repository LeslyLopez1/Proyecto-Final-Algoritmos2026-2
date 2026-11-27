package com.schooltracker.proyectofinal.Model;

import com.schooltracker.proyectofinal.Algoritmos.BFS;
import com.schooltracker.proyectofinal.Algoritmos.CriteriosOrden;
import com.schooltracker.proyectofinal.Algoritmos.DFS;
import com.schooltracker.proyectofinal.Algoritmos.Dijkstra;
import com.schooltracker.proyectofinal.Algoritmos.QuickSort;
import com.schooltracker.proyectofinal.Algoritmos.ShellSort;
import com.schooltracker.proyectofinal.Model.Archivos.Lector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto 6 (análisis empírico): mide cuánto tarda cada operación con grafos de distintos tamaños.
 * Usa las clases reales del proyecto (Lector, Grafo, Dijkstra, QuickSort y ShellSort).
 */
public class AnalisisEmpirico {

    // Archivos de prueba (carpeta resources/CSV): grafo pequeño, mediano y grande (20, 100 y 500 vértices).
    // Si se cambian los CSV por otros, el análisis los usa igual mientras conserven estos nombres.
    public static final String[] ARCHIVOS = {
            "grafo_peque.csv",
            "grafo_mediano.csv",
            "grafo_grande.csv"
    };

    // Nombre que se muestra para cada archivo (mismo orden que ARCHIVOS)
    private static final String[] NOMBRES = {"Pequeño", "Mediano", "Grande"};

    // Nombres de las barras de la gráfica, en el mismo orden que las columnas del resultado
    public static final String[] OPERACIONES = {
            "Importación CSV", "Construcción", "BFS", "DFS", "Dijkstra", "QuickSort", "Shell Sort"
    };

    private static final int REPETICIONES = 5;

    /** Mide todo y regresa los tiempos en milisegundos. */
    public static ResultadoAnalisis ejecutar() throws IOException {
        double[][] tiempos = new double[ARCHIVOS.length][OPERACIONES.length];
        String[] etiquetas = new String[ARCHIVOS.length];

        for (int i = 0; i < ARCHIVOS.length; i++) {
            final String ruta = "/CSV/" + ARCHIVOS[i];
            final Lector lector = new Lector();

            // El grafo se lee una vez para tener con qué probar las demás operaciones
            final Grafo grafo = lector.leerDesdeRecurso(ruta, true);
            final List<Vertice> vertices = grafo.getVertices();
            // Origen: el vértice con más conexiones de salida, para que Dijkstra recorra casi todo el grafo
            Vertice masConectado = vertices.get(0);
            for (Vertice v : vertices) {
                if (grafo.getAdyacentes(v.getId()).size() > grafo.getAdyacentes(masConectado.getId()).size()) {
                    masConectado = v;
                }
            }
            final int origenId = masConectado.getId();
            final int destinoId = vertices.get(vertices.size() - 1).getId();
            final List<Arista> aristas = grafo.getTodasLasAristas();
            etiquetas[i] = NOMBRES[i] + " (" + vertices.size() + " vértices, " + aristas.size() + " aristas)";

            tiempos[i][0] = medir(() -> {
                try {
                    lector.leerDesdeRecurso(ruta, true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            tiempos[i][1] = medir(() -> {
                Grafo copia = new Grafo(true);
                for (Vertice v : vertices) {
                    copia.agregarVertice(new Vertice(v.getId(), v.getEtiqueta()));
                }
                for (Arista a : aristas) {
                    copia.agregarArista(a.getOrigen().getId(), a.getDestino().getId(), a.getPeso());
                }
            });

            tiempos[i][2] = medir(() -> BFS.recorrer(grafo, origenId));

            tiempos[i][3] = medir(() -> DFS.recorrer(grafo, origenId));

            tiempos[i][4] = medir(() -> Dijkstra.calcular(grafo, origenId, destinoId));

            // Cada vez se ordena una copia desordenada (la copia es parte del tiempo, pero es mínima)
            tiempos[i][5] = medir(() -> {
                List<Arista> copia = new ArrayList<>(aristas);
                QuickSort.ordenar(copia, CriteriosOrden.POR_PESO);
            });

            tiempos[i][6] = medir(() -> {
                List<Arista> copia = new ArrayList<>(aristas);
                ShellSort.ordenar(copia, CriteriosOrden.POR_PESO);
            });
        }
        return new ResultadoAnalisis(etiquetas, tiempos);
    }

    /** Ejecuta la tarea una vez de calentamiento y luego varias veces; regresa el promedio en ms. */
    private static double medir(Runnable tarea) {
        tarea.run();
        long inicio = System.nanoTime();
        for (int i = 0; i < REPETICIONES; i++) {
            tarea.run();
        }
        long fin = System.nanoTime();
        return (fin - inicio) / 1000000.0 / REPETICIONES;
    }
}
