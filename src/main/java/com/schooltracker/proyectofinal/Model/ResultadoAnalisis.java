package com.schooltracker.proyectofinal.Model;

/** Resultado del análisis empírico: una etiqueta por archivo y los tiempos en milisegundos. */
public class ResultadoAnalisis {
    private final String[] etiquetas;   // por ejemplo "Pequeño (20 vértices, 45 aristas)"
    private final double[][] tiempos;   // tiempos[archivo][operación]

    public ResultadoAnalisis(String[] etiquetas, double[][] tiempos) {
        this.etiquetas = etiquetas;
        this.tiempos = tiempos;
    }

    public String[] getEtiquetas() { return etiquetas; }
    public double[][] getTiempos() { return tiempos; }
}
