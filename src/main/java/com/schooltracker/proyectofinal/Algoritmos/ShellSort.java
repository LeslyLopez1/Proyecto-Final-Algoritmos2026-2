package com.schooltracker.proyectofinal.Algoritmos;

import com.schooltracker.proyectofinal.Model.Arista;

import java.util.Comparator;
import java.util.List;

/**
 * Shell Sort tal como se vio en clase (apuntes "Ordenamientos 1/2"):
 * se comparan elementos separados por "inter" posiciones, y ese salto se va
 * dividiendo entre 2 (n/2, n/4, ..., 1) hasta ordenar todo el arreglo.
 */
public class ShellSort {

    /** criterio: CriteriosOrden.POR_PESO o CriteriosOrden.POR_NOMBRE */
    public static void ordenar(List<Arista> a, Comparator<Arista> criterio) {
        int n = a.size();

        // 1. Hacer inter = n + 1
        int inter = n + 1;

        // 2. Repetir mientras inter > 1
        while (inter > 1) {
            inter = inter / 2;
            boolean band = true;

            // 2.1 Repetir mientras band sea verdadero (hubo intercambios)
            while (band) {
                band = false;
                int i = 0;

                // 2.1.1 Recorrer comparando a[i] con a[i + inter]
                while (i + inter < n) {
                    if (criterio.compare(a.get(i), a.get(i + inter)) > 0) {
                        Arista auxiliar = a.get(i);        // intercambiar
                        a.set(i, a.get(i + inter));
                        a.set(i + inter, auxiliar);
                        band = true;
                    }
                    i = i + 1;
                }
            }
        }
    }
}
