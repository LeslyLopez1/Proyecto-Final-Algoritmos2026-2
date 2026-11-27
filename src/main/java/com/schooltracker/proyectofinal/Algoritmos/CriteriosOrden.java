package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Vertice;

import java.util.Comparator;

/** Criterios para ordenar las conexiones: por nombre de los vértices o por peso. */
public class CriteriosOrden {

    /** Por nombre del vértice de origen y, si empatan, por el de destino. */
    public static final Comparator<Arista> POR_NOMBRE = (a, b) -> {
        int porOrigen = compararVertices(a.getOrigen(), b.getOrigen());
        if (porOrigen != 0) {
            return porOrigen;
        }
        return compararVertices(a.getDestino(), b.getDestino());
    };

    /** Por peso de menor a mayor y, si empatan, por nombre. */
    public static final Comparator<Arista> POR_PESO = (a, b) -> {
        int porPeso = Double.compare(a.getPeso(), b.getPeso());
        if (porPeso != 0) {
            return porPeso;
        }
        return POR_NOMBRE.compare(a, b);
    };

    /**
     * Si los dos vértices usan su número como etiqueta (lo normal), se comparan como
     * números (así el 2 va antes que el 10). Si tienen nombre, se comparan alfabéticamente.
     */
    private static int compararVertices(Vertice a, Vertice b) {
        boolean aSinNombre = a.getEtiqueta().equals(String.valueOf(a.getId()));
        boolean bSinNombre = b.getEtiqueta().equals(String.valueOf(b.getId()));

        if (aSinNombre && bSinNombre) {
            return Integer.compare(a.getId(), b.getId());
        }
        int porNombre = a.getEtiqueta().compareToIgnoreCase(b.getEtiqueta());
        if (porNombre != 0) {
            return porNombre;
        }
        return Integer.compare(a.getId(), b.getId());
    }
}
