package com.schooltracker.proyectofinal.Model.Archivos;

import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Grafo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Guarda un grafo en un CSV con el mismo formato que lee el Lector: origen,destino,peso.
 * Los vértices aislados (sin conexiones) no se escriben, porque el formato solo guarda conexiones.
 */
public class Escritor {

    /** Regresa cuántas conexiones se guardaron. */
    public int guardar(Grafo grafo, File archivo) throws IOException {
        int guardadas = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8))) {
            writer.write("origen,destino,peso");
            writer.newLine();

            for (Arista a : grafo.getTodasLasAristas()) {
                String origen = a.getOrigen().getEtiqueta();
                String destino = a.getDestino().getEtiqueta();
                if (origen.contains(",") || destino.contains(",")) {
                    throw new IllegalArgumentException("El nombre de un vértice no puede llevar comas: "
                            + origen + " / " + destino);
                }
                writer.write(origen + "," + destino + "," + formatoPeso(a.getPeso()));
                writer.newLine();
                guardadas++;
            }
        }
        return guardadas;
    }

    /** 100.0 se escribe como 100; 2.5 se queda como 2.5. */
    private String formatoPeso(double peso) {
        if (peso == Math.rint(peso) && Math.abs(peso) < 1e15) {
            return String.valueOf((long) peso);
        }
        return String.valueOf(peso);
    }
}
