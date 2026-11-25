package com.schooltracker.proyectofinal.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa un grafo de ubicaciones con IDs enteros.
 * Mantiene sincronizadas tanto la lista de adyacencia como la matriz de adyacencia,
 * y soporta grafos dirigidos y no dirigidos.
 */
public class Grafo {
    // true = las aristas tienen dirección (u -> v); false = bidireccionales (u <-> v)
    private boolean dirigido;
    // Catálogo de vértices indexado por su ID entero
    private final Map<Integer, Vertice> vertices;
    // Lista de adyacencia: para cada ID de vértice, guarda sus aristas salientes
    private final Map<Integer, List<Arista>> listaAdyacencia;
    // Mantiene el orden de inserción de los vértices para mapear filas/columnas de la matriz
    private final List<Integer> ordenIndices;
    // Matriz de adyacencia cuadrada de tamaño N x N (0.0 representa ausencia de arista)
    private double[][] matrizAdyacencia;

    /**
     * Inicializa un grafo vacío en modo dirigido o no dirigido.
     */
    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
        this.vertices = new HashMap<>();
        this.listaAdyacencia = new HashMap<>();
        this.ordenIndices = new ArrayList<>();
        this.matrizAdyacencia = new double[0][0];
    }

    // Indica si el grafo es dirigido
    public boolean isDirigido() {
        return dirigido;
    }

    // Cambia entre dirigido y no dirigido y reconstruye las estructuras para reflejar el cambio
    public void setDirigido(boolean dirigido) {
        if (this.dirigido == dirigido) return;
        this.dirigido = dirigido;
        reconstruirEstructurasDesdeAristas();
    }

    // Agrega un vértice nuevo usando únicamente su ID entero
    public boolean agregarVertice(int id) {
        return agregarVertice(new Vertice(id));
    }

    // Agrega un objeto Vertice si no es nulo y su ID aún no existe en el grafo
    public boolean agregarVertice(Vertice vertice) {
        if (vertice == null || vertices.containsKey(vertice.getId())) {
            return false;
        }
        vertices.put(vertice.getId(), vertice);
        listaAdyacencia.put(vertice.getId(), new ArrayList<>());
        ordenIndices.add(vertice.getId());
        // Amplía la matriz en una fila y una columna para el nuevo vértice
        redimensionarMatrizAgregandoVertice();
        return true;
    }

    /**
     * Agrega una conexión entre origenId y destinoId con el peso indicado.
     * Crea automáticamente los vértices si aún no existen, pero rechaza auto-bucles,
     * pesos negativos y aristas duplicadas directas.
     */
    public boolean agregarArista(int origenId, int destinoId, double peso) {
        if (origenId == destinoId || peso < 0) {
            return false;
        }
        // Asegura que ambos vértices existan en el grafo
        agregarVertice(origenId);
        agregarVertice(destinoId);

        Vertice origen = vertices.get(origenId);
        Vertice destino = vertices.get(destinoId);

        // Evita duplicar una conexión ya existente en ese sentido
        if (existeAristaDirecta(origenId, destinoId)) {
            return false;
        }

        // Registra la conexión origen -> destino en lista y matriz
        Arista ida = new Arista(origen, destino, peso);
        listaAdyacencia.get(origenId).add(ida);
        actualizarCeldaMatriz(origenId, destinoId, peso);

        // Si el grafo no es dirigido, registra también la conexión inversa destino -> origen
        if (!dirigido) {
            if (!existeAristaDirecta(destinoId, origenId)) {
                Arista vuelta = new Arista(destino, origen, peso);
                listaAdyacencia.get(destinoId).add(vuelta);
            }
            actualizarCeldaMatriz(destinoId, origenId, peso);
        }
        return true;
    }

    // Modifica el peso de una arista existente (y de su inversa si el grafo es no dirigido)
    public boolean modificarPesoArista(int origenId, int destinoId, double nuevoPeso) {
        if (nuevoPeso < 0 || !existeAristaDirecta(origenId, destinoId)) {
            return false;
        }
        for (Arista a : listaAdyacencia.get(origenId)) {
            if (a.getDestino().getId() == destinoId) {
                a.setPeso(nuevoPeso);
            }
        }
        actualizarCeldaMatriz(origenId, destinoId, nuevoPeso);

        if (!dirigido && listaAdyacencia.containsKey(destinoId)) {
            for (Arista a : listaAdyacencia.get(destinoId)) {
                if (a.getDestino().getId() == origenId) {
                    a.setPeso(nuevoPeso);
                }
            }
            actualizarCeldaMatriz(destinoId, origenId, nuevoPeso);
        }
        return true;
    }

    // Elimina una arista entre dos vértices y limpia su celda en la matriz
    public boolean eliminarArista(int origenId, int destinoId) {
        if (!listaAdyacencia.containsKey(origenId)) return false;
        boolean removida = listaAdyacencia.get(origenId).removeIf(a -> a.getDestino().getId() == destinoId);
        if (removida) {
            actualizarCeldaMatriz(origenId, destinoId, 0.0);
        }
        // Si es no dirigido, también quita la conexión en sentido contrario
        if (!dirigido && listaAdyacencia.containsKey(destinoId)) {
            boolean removidaVuelta = listaAdyacencia.get(destinoId).removeIf(a -> a.getDestino().getId() == origenId);
            if (removidaVuelta) {
                actualizarCeldaMatriz(destinoId, origenId, 0.0);
                removida = true;
            }
        }
        return removida;
    }

    // Elimina un vértice, todas las aristas incidentes hacia él y reconstruye la matriz
    public boolean eliminarVertice(int id) {
        if (!vertices.containsKey(id)) return false;
        vertices.remove(id);
        listaAdyacencia.remove(id);
        ordenIndices.remove(Integer.valueOf(id));

        // Quita cualquier arista de otros vértices que apunte al vértice eliminado
        for (List<Arista> ady : listaAdyacencia.values()) {
            ady.removeIf(a -> a.getDestino().getId() == id);
        }
        reconstruirMatrizDesdeLista();
        return true;
    }

    /**
     * Verifica si un vértice tiene al menos una conexión (saliente o entrante).
     * Útil para omitir vértices aislados al exportar el grafo a CSV.
     */
    public boolean tieneConexiones(int id) {
        List<Arista> ady = listaAdyacencia.get(id);
        if (ady != null && !ady.isEmpty()) {
            return true;
        }
        if (dirigido) {
            for (List<Arista> otras : listaAdyacencia.values()) {
                for (Arista a : otras) {
                    if (a.getDestino().getId() == id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Devuelve una copia en lista de todos los vértices registrados
    public List<Vertice> getVertices() {
        return new ArrayList<>(vertices.values());
    }

    // Busca y devuelve un vértice por su ID, o null si no existe
    public Vertice getVertice(int id) {
        return vertices.get(id);
    }

    // Devuelve las aristas salientes de un vértice dado (en formato de solo lectura)
    public List<Arista> getAdyacentes(int id) {
        return Collections.unmodifiableList(listaAdyacencia.getOrDefault(id, List.of()));
    }

    // Devuelve las aristas del grafo (evitando duplicar pares u-v y v-u cuando no es dirigido)
    public List<Arista> getTodasLasAristas() {
        List<Arista> todas = new ArrayList<>();
        for (Map.Entry<Integer, List<Arista>> entry : listaAdyacencia.entrySet()) {
            int u = entry.getKey();
            for (Arista a : entry.getValue()) {
                if (dirigido || u < a.getDestino().getId()) {
                    todas.add(a);
                }
            }
        }
        return todas;
    }

    // Devuelve la matriz de adyacencia actual
    public double[][] getMatrizAdyacencia() {
        return matrizAdyacencia;
    }

    // Devuelve la lista de IDs que indica qué fila y columna corresponde a cada vértice en la matriz
    public List<Integer> getOrdenIndicesMatriz() {
        return Collections.unmodifiableList(ordenIndices);
    }

    // Comprueba si ya existe una arista directa desde origenId hacia destinoId
    private boolean existeAristaDirecta(int origenId, int destinoId) {
        List<Arista> ady = listaAdyacencia.get(origenId);
        if (ady == null) return false;
        for (Arista a : ady) {
            if (a.getDestino().getId() == destinoId) {
                return true;
            }
        }
        return false;
    }

    // Crea una matriz (n)x(n) copiando los valores previos de (n-1)x(n-1)
    private void redimensionarMatrizAgregandoVertice() {
        int n = ordenIndices.size();
        double[][] nueva = new double[n][n];
        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(matrizAdyacencia[i], 0, nueva[i], 0, n - 1);
        }
        matrizAdyacencia = nueva;
    }

    // Escribe un peso (o 0.0 si se elimina) en la posición [fila][columna] correspondiente
    private void actualizarCeldaMatriz(int origenId, int destinoId, double valor) {
        int i = ordenIndices.indexOf(origenId);
        int j = ordenIndices.indexOf(destinoId);
        if (i >= 0 && j >= 0) {
            matrizAdyacencia[i][j] = valor;
        }
    }

    // Recalcula por completo la matriz de adyacencia a partir de la lista de adyacencia
    private void reconstruirMatrizDesdeLista() {
        int n = ordenIndices.size();
        matrizAdyacencia = new double[n][n];
        for (Map.Entry<Integer, List<Arista>> entry : listaAdyacencia.entrySet()) {
            for (Arista a : entry.getValue()) {
                actualizarCeldaMatriz(a.getOrigen().getId(), a.getDestino().getId(), a.getPeso());
            }
        }
    }

    // Limpia y vuelve a poblar lista y matriz de adyacencia respetando el valor actual de 'dirigido'
    private void reconstruirEstructurasDesdeAristas() {
        List<Arista> aristasActuales = getTodasLasAristas();
        for (Integer id : listaAdyacencia.keySet()) {
            listaAdyacencia.get(id).clear();
        }
        int n = ordenIndices.size();
        matrizAdyacencia = new double[n][n];

        for (Arista a : aristasActuales) {
            int u = a.getOrigen().getId();
            int v = a.getDestino().getId();
            double p = a.getPeso();
            Arista ida = new Arista(vertices.get(u), vertices.get(v), p);
            listaAdyacencia.get(u).add(ida);
            actualizarCeldaMatriz(u, v, p);
            if (!dirigido) {
                Arista vuelta = new Arista(vertices.get(v), vertices.get(u), p);
                listaAdyacencia.get(v).add(vuelta);
                actualizarCeldaMatriz(v, u, p);
            }
        }
    }
}