package com.schooltracker.proyectofinal.Model.Archivos;

import com.schooltracker.proyectofinal.Model.Grafo;

import java.util.List;

/** Lo que regresa el Lector: el grafo construido, cuántos vértices y aristas se importaron y los errores. */
public class ResultadoImportacion {
    private final Grafo grafo;
    private final int vertices;
    private final int aristas;
    private final List<String> errores;

    public ResultadoImportacion(Grafo grafo, int vertices, int aristas, List<String> errores) {
        this.grafo = grafo;
        this.vertices = vertices;
        this.aristas = aristas;
        this.errores = errores;
    }

    public Grafo getGrafo() { return grafo; }
    public int getVertices() { return vertices; }
    public int getAristas() { return aristas; }
    public List<String> getErrores() { return errores; }
    public boolean tieneErrores() { return !errores.isEmpty(); }
}
