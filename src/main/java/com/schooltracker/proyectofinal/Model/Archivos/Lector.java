package com.schooltracker.proyectofinal.Model.Archivos;

import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.Vertice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Lector {

    public static final String RUTA_GRAFO_PEQUE = "/CSV/grafo_peque.csv";

    public Grafo leerGrafoPeque(boolean dirigido) throws IOException {
        return leerDesdeRecurso(RUTA_GRAFO_PEQUE, dirigido);
    }

    public Grafo leerDesdeRecurso(String rutaRecurso, boolean dirigido) throws IOException {
        if (rutaRecurso == null || rutaRecurso.isBlank()) {
            throw new IllegalArgumentException("La ruta del recurso CSV no puede estar vacía.");
        }

        InputStream inputStream = Lector.class.getResourceAsStream(rutaRecurso);
        if (inputStream == null) {
            throw new IOException("No se encontró el archivo CSV en recursos: " + rutaRecurso);
        }

        Grafo grafo = new Grafo(dirigido);
        Map<String, Integer> idsPorNombre = new HashMap<>();
        int siguienteId = 1;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String linea;
            int numeroLinea = 0;
            boolean cabeceraValidada = false;

            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                String limpia = linea.trim();
                if (limpia.isEmpty()) {
                    continue;
                }

                String[] columnas = limpia.split(",", -1);
                if (columnas.length != 3) {
                    throw new IllegalArgumentException(
                            "Formato inválido en línea " + numeroLinea + ": se esperaban 3 columnas."
                    );
                }

                String col0 = columnas[0].trim();
                String col1 = columnas[1].trim();
                String col2 = columnas[2].trim();

                if (!cabeceraValidada) {
                    cabeceraValidada = true;
                    if (col0.equalsIgnoreCase("origen")
                            && col1.equalsIgnoreCase("destino")
                            && col2.equalsIgnoreCase("peso")) {
                        continue;
                    }
                }

                if (col0.isEmpty() || col1.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Vértice vacío en línea " + numeroLinea + "."
                    );
                }

                double peso;
                try {
                    peso = Double.parseDouble(col2);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Peso no numérico en línea " + numeroLinea + ": '" + col2 + "'.", e
                    );
                }

                if (peso < 0 || Double.isNaN(peso) || Double.isInfinite(peso)) {
                    throw new IllegalArgumentException(
                            "El peso debe ser un número no negativo en línea " + numeroLinea + "."
                    );
                }

                int origenId = obtenerORegistrarVertice(grafo, idsPorNombre, col0, siguienteId);
                if (idsPorNombre.get(col0) == siguienteId) {
                    siguienteId++;
                }

                int destinoId = obtenerORegistrarVertice(grafo, idsPorNombre, col1, siguienteId);
                if (idsPorNombre.get(col1) == siguienteId) {
                    siguienteId++;
                }

                if (origenId == destinoId) {
                    throw new IllegalArgumentException(
                            "Auto-bucle no permitido en línea " + numeroLinea + ": '" + col0 + "'."
                    );
                }

                boolean agregada = grafo.agregarArista(origenId, destinoId, peso);
                if (!agregada) {
                    throw new IllegalArgumentException(
                            "Arista duplicada o inválida en línea " + numeroLinea + ": "
                                    + col0 + " -> " + col1
                    );
                }
            }
        }

        return grafo;
    }

    private int obtenerORegistrarVertice(
            Grafo grafo,
            Map<String, Integer> idsPorNombre,
            String nombreUbicacion,
            int idCandidato
    ) {
        Integer idExistente = idsPorNombre.get(nombreUbicacion);
        if (idExistente != null) {
            return idExistente;
        }
        idsPorNombre.put(nombreUbicacion, idCandidato);
        grafo.agregarVertice(new Vertice(idCandidato, nombreUbicacion));
        return idCandidato;
    }
}