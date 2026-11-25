package com.schooltracker.proyectofinal.Model;

import java.util.Comparator;
import java.util.List;

public class QuickSort {

    //este metodo se creo por:
    //El usuario podrá ordenar las conexiones de acuerdo con:
    //Peso de las aristas.
    //Nombre de los vértices
    public static void ordenar(List<Arista> lista, Comparator<Arista> criterio) {
        if (lista.size() > 1) {
            recursivo(lista, 0, lista.size() - 1, criterio);
        }
    }

    private static void recursivo(List<Arista> A, int ini, int fin, Comparator<Arista> criterio) {
        int izq = ini;
        int der = fin;
        int pos = ini;
        boolean band = true;

        while (band) {
            band = false;

            while (criterio.compare(A.get(pos), A.get(der)) <= 0 && pos != der) {
                der = der - 1;
            }

            if (pos != der) {
                intercambiar(A, pos, der);
                pos = der;

                while (criterio.compare(A.get(pos), A.get(izq)) >= 0 && pos != izq) {
                    izq = izq + 1;
                }

                if (pos != izq) {
                    band = true;
                    intercambiar(A, pos, izq);
                    pos = izq;
                }
            }
        }

        if (pos - 1 > ini) {
            recursivo(A, ini, pos - 1, criterio);
        }
        if (fin > pos + 1) {
            recursivo(A, pos + 1, fin, criterio);
        }
    }

    private static void intercambiar(List<Arista> A, int i, int j) {
        Arista auxiliar = A.get(i);
        A.set(i, A.get(j));
        A.set(j, auxiliar);
    }
}