package com.schooltracker.proyectofinal.Model;

import java.util.Comparator;
import java.util.List;

 //ShellSort tal como se vio en clase se comparan elementos separados
 //por "inter" posiciones, y ese salto se va dividiendo hasta ordenar todo el arreglo.
public class ShellSort {

    //este metodo se creo por:
    //El usuario podrá ordenar las conexiones de acuerdo con:
    //Peso de las aristas.
    //Nombre de los vértices
    public static void ordenar(List<Arista> a, Comparator<Arista> criterio) {
        int n = a.size();

        int inter = n + 1;

        while (inter > 1) {
            inter = inter / 2;
            boolean band = true;

            while (band) {
                band = false;
                int i = 0;

                while (i + inter < n) {
                    if (criterio.compare(a.get(i), a.get(i + inter)) > 0) {
                        Arista auxiliar = a.get(i);
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