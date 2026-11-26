package com.schooltracker.proyectofinal.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafo {
    private boolean dirigido;
    private final Map<Integer, Vertice> vertices;
    private final Map<Integer, List<Arista>> listaAdyacencia;
    private final List<Arista> aristasRegistradas;
    private final List<Integer> ordenIndices;
    private double[][] matrizAdyacencia;

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
        this.vertices = new HashMap<>();
        this.listaAdyacencia = new HashMap<>();
        this.aristasRegistradas = new ArrayList<>();
        this.ordenIndices = new ArrayList<>();
        this.matrizAdyacencia = new double[0][0];
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public void setDirigido(boolean dirigido) {
        if (this.dirigido == dirigido) return;
        this.dirigido = dirigido;
        reconstruirEstructurasDesdeAristas();
    }

    public boolean agregarVertice(int id) {
        return agregarVertice(new Vertice(id));
    }

    public boolean agregarVertice(Vertice vertice) {
        if (vertice == null || vertices.containsKey(vertice.getId())) {
            return false;
        }
        vertices.put(vertice.getId(), vertice);
        listaAdyacencia.put(vertice.getId(), new ArrayList<>());
        ordenIndices.add(vertice.getId());
        redimensionarMatrizAgregandoVertice();
        return true;
    }

    public boolean agregarArista(int origenId, int destinoId, double peso) {
        if (origenId == destinoId || peso < 0) {
            return false;
        }
        agregarVertice(origenId);
        agregarVertice(destinoId);

        Vertice origen = vertices.get(origenId);
        Vertice destino = vertices.get(destinoId);

        if (existeAristaDirecta(origenId, destinoId)) {
            return false;
        }

        Arista ida = new Arista(origen, destino, peso);
        listaAdyacencia.get(origenId).add(ida);
        aristasRegistradas.add(new Arista(origen, destino, peso));
        actualizarCeldaMatriz(origenId, destinoId, peso);

        if (!dirigido) {
            if (!existeAristaDirecta(destinoId, origenId)) {
                Arista vuelta = new Arista(destino, origen, peso);
                listaAdyacencia.get(destinoId).add(vuelta);
            }
            actualizarCeldaMatriz(destinoId, origenId, peso);
        }
        return true;
    }

    public boolean modificarPesoArista(int origenId, int destinoId, double nuevoPeso) {
        if (nuevoPeso < 0 || !existeAristaDirecta(origenId, destinoId)) {
            return false;
        }
        for (Arista a : listaAdyacencia.get(origenId)) {
            if (a.getDestino().getId() == destinoId) {
                a.setPeso(nuevoPeso);
            }
        }
        for (Arista a : aristasRegistradas) {
            if (a.getOrigen().getId() == origenId && a.getDestino().getId() == destinoId) {
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
            for (Arista a : aristasRegistradas) {
                if (a.getOrigen().getId() == destinoId && a.getDestino().getId() == origenId) {
                    a.setPeso(nuevoPeso);
                }
            }
            actualizarCeldaMatriz(destinoId, origenId, nuevoPeso);
        }
        return true;
    }

    public boolean eliminarArista(int origenId, int destinoId) {
        if (!listaAdyacencia.containsKey(origenId)) return false;
        boolean removida = listaAdyacencia.get(origenId).removeIf(a -> a.getDestino().getId() == destinoId);
        aristasRegistradas.removeIf(a -> a.getOrigen().getId() == origenId && a.getDestino().getId() == destinoId);
        if (removida) {
            actualizarCeldaMatriz(origenId, destinoId, 0.0);
        }
        if (!dirigido && listaAdyacencia.containsKey(destinoId)) {
            boolean removidaVuelta = listaAdyacencia.get(destinoId).removeIf(a -> a.getDestino().getId() == origenId);
            aristasRegistradas.removeIf(a -> a.getOrigen().getId() == destinoId && a.getDestino().getId() == origenId);
            if (removidaVuelta) {
                actualizarCeldaMatriz(destinoId, origenId, 0.0);
                removida = true;
            }
        }
        return removida;
    }

    public boolean eliminarVertice(int id) {
        if (!vertices.containsKey(id)) return false;
        vertices.remove(id);
        listaAdyacencia.remove(id);
        aristasRegistradas.removeIf(a -> a.getOrigen().getId() == id || a.getDestino().getId() == id);
        ordenIndices.remove(Integer.valueOf(id));

        for (List<Arista> ady : listaAdyacencia.values()) {
            ady.removeIf(a -> a.getDestino().getId() == id);
        }
        reconstruirMatrizDesdeLista();
        return true;
    }

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

    public List<Vertice> getVertices() {
        return new ArrayList<>(vertices.values());
    }

    public Vertice getVertice(int id) {
        return vertices.get(id);
    }

    public List<Arista> getAdyacentes(int id) {
        return Collections.unmodifiableList(listaAdyacencia.getOrDefault(id, List.of()));
    }

    public List<Arista> getTodasLasAristas() {
        if (dirigido) {
            List<Arista> todas = new ArrayList<>();
            for (List<Arista> aristas : listaAdyacencia.values()) {
                todas.addAll(aristas);
            }
            return todas;
        }

        List<Arista> noDirigidas = new ArrayList<>();
        for (Map.Entry<Integer, List<Arista>> entry : listaAdyacencia.entrySet()) {
            int u = entry.getKey();
            for (Arista a : entry.getValue()) {
                if (u < a.getDestino().getId()) {
                    noDirigidas.add(a);
                }
            }
        }
        return noDirigidas;
    }

    public double[][] getMatrizAdyacencia() {
        return matrizAdyacencia;
    }

    public List<Integer> getOrdenIndicesMatriz() {
        return Collections.unmodifiableList(ordenIndices);
    }

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

    private void redimensionarMatrizAgregandoVertice() {
        int n = ordenIndices.size();
        double[][] nueva = new double[n][n];
        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(matrizAdyacencia[i], 0, nueva[i], 0, n - 1);
        }
        matrizAdyacencia = nueva;
    }

    private void actualizarCeldaMatriz(int origenId, int destinoId, double valor) {
        int i = ordenIndices.indexOf(origenId);
        int j = ordenIndices.indexOf(destinoId);
        if (i >= 0 && j >= 0) {
            matrizAdyacencia[i][j] = valor;
        }
    }

    private void reconstruirMatrizDesdeLista() {
        int n = ordenIndices.size();
        matrizAdyacencia = new double[n][n];
        for (Map.Entry<Integer, List<Arista>> entry : listaAdyacencia.entrySet()) {
            for (Arista a : entry.getValue()) {
                actualizarCeldaMatriz(a.getOrigen().getId(), a.getDestino().getId(), a.getPeso());
            }
        }
    }

    private void reconstruirEstructurasDesdeAristas() {
        for (Integer id : listaAdyacencia.keySet()) {
            listaAdyacencia.get(id).clear();
        }
        int n = ordenIndices.size();
        matrizAdyacencia = new double[n][n];

        for (Arista a : aristasRegistradas) {
            int u = a.getOrigen().getId();
            int v = a.getDestino().getId();
            double p = a.getPeso();

            if (!existeAristaDirecta(u, v)) {
                listaAdyacencia.get(u).add(new Arista(vertices.get(u), vertices.get(v), p));
            }
            actualizarCeldaMatriz(u, v, p);

            if (!dirigido) {
                if (!existeAristaDirecta(v, u)) {
                    listaAdyacencia.get(v).add(new Arista(vertices.get(v), vertices.get(u), p));
                }
                actualizarCeldaMatriz(v, u, p);
            }
        }
    }
}