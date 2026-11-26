package com.schooltracker.proyectofinal.Model;

import java.util.List;

//Esta clase guarda la respuesta de Dijkstra.
public class Resultado {
    private Vertice origen;
    private Vertice destino;
    //vertices de ruta en orden
    private List<Vertice> camino;
    //suma de los pesos de la ruta
    private double costo;
    //false si no hay ruta
    private boolean encontrada;

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

    //Ruta: "1 -> 2 -> 4 -> 6"
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