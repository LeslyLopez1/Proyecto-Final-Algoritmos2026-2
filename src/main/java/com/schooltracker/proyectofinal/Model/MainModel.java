
package com.schooltracker.proyectofinal.Model;

import java.util.List;

public class MainModel {

    private Grafo grafoActual;

    public MainModel() {
        this(true);
    }

    public MainModel(boolean dirigidoPorDefecto) {
        this.grafoActual = new Grafo(dirigidoPorDefecto);
    }

    public Grafo getGrafoActual() {
        return grafoActual;
    }

    public void reiniciarGrafo(boolean dirigido) {
        this.grafoActual = new Grafo(dirigido);
    }

    public void establecerGrafo(Grafo nuevoGrafo) {
        if (nuevoGrafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser nulo.");
        }
        this.grafoActual = nuevoGrafo;
    }

    public boolean isDirigido() {
        return grafoActual.isDirigido();
    }

    public void setDirigido(boolean dirigido) {
        grafoActual.setDirigido(dirigido);
    }

    public boolean agregarVertice(int id) {
        return grafoActual.agregarVertice(id);
    }

    public boolean agregarVertice(int id, String etiqueta) {
        return grafoActual.agregarVertice(new Vertice(id, etiqueta));
    }

    public boolean modificarEtiquetaVertice(int id, String nuevaEtiqueta) {
        Vertice v = grafoActual.getVertice(id);
        if (v == null || nuevaEtiqueta == null || nuevaEtiqueta.isBlank()) {
            return false;
        }
        v.setEtiqueta(nuevaEtiqueta);
        return true;
    }

    public boolean eliminarVertice(int id) {
        return grafoActual.eliminarVertice(id);
    }

    public boolean agregarArista(int origenId, int destinoId, double peso) {
        return grafoActual.agregarArista(origenId, destinoId, peso);
    }

    public boolean modificarPesoArista(int origenId, int destinoId, double nuevoPeso) {
        return grafoActual.modificarPesoArista(origenId, destinoId, nuevoPeso);
    }

    public boolean eliminarArista(int origenId, int destinoId) {
        return grafoActual.eliminarArista(origenId, destinoId);
    }

    public List<Vertice> getVertices() {
        return grafoActual.getVertices();
    }

    public List<Arista> getTodasLasAristas() {
        return grafoActual.getTodasLasAristas();
    }

    public List<Arista> getAdyacentes(int idVertice) {
        return grafoActual.getAdyacentes(idVertice);
    }

    public double[][] getMatrizAdyacencia() {
        return grafoActual.getMatrizAdyacencia();
    }

    public List<Integer> getOrdenIndicesMatriz() {
        return grafoActual.getOrdenIndicesMatriz();
    }
}