package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Arista;

import java.util.Comparator;
import java.util.List;

/**
 * QuickSort tal como se vio en clase (apuntes "Ordenamientos 2/2"):
 * el pivote es el primer elemento de cada tramo, se coloca en su posición correcta
 * moviendo "der" e "izq", y luego se repite para la parte izquierda y la derecha.
 */
public class QuickSort {

    /** criterio: CriteriosOrden.POR_PESO o CriteriosOrden.POR_NOMBRE */
    public static void ordenar(List<Arista> lista, Comparator<Arista> criterio) {
        if (lista.size() > 1) {
            recursivo(lista, 0, lista.size() - 1, criterio);
        }
    }

    private static void recursivo(List<Arista> A, int ini, int fin, Comparator<Arista> criterio) {
        // 1. Hacer izq=ini, der=fin, pos=ini y band=verdadero
        int izq = ini;
        int der = fin;
        int pos = ini;
        boolean band = true;

        // 2. Repetir mientras band sea verdadero
        while (band) {
            band = false;

            // 2.1 Mover "der" hacia la izquierda mientras A[pos] <= A[der]
            while (criterio.compare(A.get(pos), A.get(der)) <= 0 && pos != der) {
                der = der - 1;
            }

            // 2.3 Si pos<>der: intercambiar A[pos] con A[der]
            if (pos != der) {
                intercambiar(A, pos, der);
                pos = der;

                // 2.3.1 Mover "izq" hacia la derecha mientras A[pos] >= A[izq]
                while (criterio.compare(A.get(pos), A.get(izq)) >= 0 && pos != izq) {
                    izq = izq + 1;
                }

                // 2.3.3 Si pos<>izq: intercambiar A[pos] con A[izq] y repetir el ciclo
                if (pos != izq) {
                    band = true;
                    intercambiar(A, pos, izq);
                    pos = izq;
                }
            }
        }

        // 4. y 6. Ordenar la parte izquierda y la parte derecha del pivote
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
