package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Vertice;

import java.util.List;

/** Guarda la respuesta de Dijkstra. */
public class Resultado {
    private Vertice origen;
    private Vertice destino;
    private List<Vertice> camino;   // vértices de la ruta, en orden
    private double costo;           // suma de los pesos de la ruta
    private boolean encontrada;     // false si no hay ruta

    public Resultado(Vertice origen, Vertice destino, List<Vertice> camino, double costo, boolean encontrada) {
        this.origen = origen;
        this.destino = destino;
        this.camino = camino;
        this.costo = costo;
        this.encontrada = encontrada;
    }

    public Vertice getOrigen() { return origen; }
    public Vertice getDestino() { return destino; }
    public List<Vertice> getCamino() { return camino; }
    public double getCosto() { return costo; }
    public boolean isEncontrada() { return encontrada; }

    /** Ruta como texto: "1 -> 2 -> 4 -> 6". */
    public String getCaminoComoTexto() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < camino.size(); i++) {
            if (i > 0) {
                texto.append(" -> ");
            }
            texto.append(camino.get(i));
        }
        return texto.toString();
    }

    @Override
    public String toString() {
        String texto = "Vértice de origen: " + origen + "\n"
                + "Vértice de destino: " + destino + "\n";
        if (!encontrada) {
            return texto + "No existe una ruta entre ambos vértices.";
        }
        return texto + "Ruta: " + getCaminoComoTexto() + "\n"
                + "Costo total: " + costo;
    }
}
