package com.schooltracker.proyectofinal.Model;

import com.schooltracker.proyectofinal.Model.Archivos.Lector;
import java.io.IOException;
import java.util.List;

public class MainModel {
    private String origenGrafoActual;
    private final Lector lector;
    private Grafo grafoActual;

    public MainModel() {
        this(true);
    }

    public MainModel(boolean dirigidoPorDefecto) {
        this.lector = new Lector();
        this.grafoActual = new Grafo(dirigidoPorDefecto);
    }

    public Grafo getGrafoActual() {
        return grafoActual;
    }

    public String getOrigenGrafoActual() {
        return origenGrafoActual;
    }

    public void reiniciarGrafo(boolean dirigido) {
        this.grafoActual = new Grafo(dirigido);
        this.origenGrafoActual = null;
    }

    public void establecerGrafo(Grafo nuevoGrafo) {
        if (nuevoGrafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser nulo.");
        }
        this.grafoActual = nuevoGrafo;
    }

    public void cargarGrafoPeque() throws IOException {
        cargarDesdeRecurso(Lector.RUTA_GRAFO_PEQUE, this.grafoActual.isDirigido());
    }

    public void cargarGrafoPeque(boolean dirigido) throws IOException {
        cargarDesdeRecurso(Lector.RUTA_GRAFO_PEQUE, dirigido);
    }

    public void cargarDesdeRecurso(String rutaRecurso, boolean dirigido) throws IOException {
        Grafo grafoCargado = lector.leerDesdeRecurso(rutaRecurso, dirigido);
        this.grafoActual = grafoCargado;
        this.origenGrafoActual = rutaRecurso;
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