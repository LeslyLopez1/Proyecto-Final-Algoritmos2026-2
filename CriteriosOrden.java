package com.schooltracker.proyectofinal.Model;

import java.util.Comparator;

//Esta clase es para ordenar las conexiones tanto por nombre de los vertices como por peso
public class CriteriosOrden {

    //Por nombre del vertice de origen y si coinciden por el de destino
    public static final Comparator<Arista> POR_NOMBRE = (a, b) -> {
        int porOrigen = compararVertices(a.getOrigen(), b.getOrigen());
        if (porOrigen != 0) {
            return porOrigen;
        }
        return compararVertices(a.getDestino(), b.getDestino());
    };

    //Por peso de menor a mayor ysi coinciden por nombre
    public static final Comparator<Arista> POR_PESO = (a, b) -> {
        int porPeso = Double.compare(a.getPeso(), b.getPeso());
        if (porPeso != 0) {
            return porPeso;
        }
        return POR_NOMBRE.compare(a, b);
    };

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
