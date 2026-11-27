package com.schooltracker.proyectofinal.Model.Archivos;

import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.Vertice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee un CSV con el formato origen,destino,peso y construye el grafo.
 * Valida: cabecera, columnas completas, vértices vacíos, pesos numéricos no negativos,
 * bucles (vértice con sí mismo) y conexiones duplicadas.
 * Complejidad: O(L) para las L líneas del archivo, más lo que cueste construir el grafo.
 */
public class Lector {
    public static final String RUTA_GRAFO_PEQUE = "/CSV/grafo_peque.csv";

    // Para no llenar la pantalla si el archivo tiene miles de errores
    private static final int MAXIMO_ERRORES = 100;

    // ---------------------------------------------------------------
    // Versiones que informan TODOS los errores (las usa la interfaz)
    // ---------------------------------------------------------------

    /** Lee un archivo cualquiera del disco (con BufferedReader y FileReader). */
    public ResultadoImportacion importarDesdeArchivo(File archivo, boolean dirigido) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            return leer(reader, dirigido);
        }
    }

    /** Lee un CSV que viene dentro del proyecto (carpeta resources). */
    public ResultadoImportacion importarDesdeRecurso(String rutaRecurso, boolean dirigido) throws IOException {
        if (rutaRecurso == null || rutaRecurso.isBlank()) {
            throw new IllegalArgumentException("La ruta del recurso CSV no puede estar vacía.");
        }
        InputStream inputStream = Lector.class.getResourceAsStream(rutaRecurso);
        if (inputStream == null) {
            throw new IOException("No se encontró el archivo CSV en recursos: " + rutaRecurso);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return leer(reader, dirigido);
        }
    }

    // ---------------------------------------------------------------
    // Versiones que lanzan excepción con el primer error (las usa el análisis)
    // ---------------------------------------------------------------

    public Grafo leerGrafoPeque(boolean dirigido) throws IOException {
        return leerDesdeRecurso(RUTA_GRAFO_PEQUE, dirigido);
    }

    public Grafo leerDesdeRecurso(String rutaRecurso, boolean dirigido) throws IOException {
        ResultadoImportacion resultado = importarDesdeRecurso(rutaRecurso, dirigido);
        if (resultado.tieneErrores()) {
            throw new IllegalArgumentException(resultado.getErrores().get(0));
        }
        return resultado.getGrafo();
    }

    // ---------------------------------------------------------------
    // Lectura y validación
    // ---------------------------------------------------------------

    private ResultadoImportacion leer(BufferedReader reader, boolean dirigido) throws IOException {
        Grafo grafo = new Grafo(dirigido);
        Map<String, Integer> idsPorNombre = new HashMap<>();
        List<String> errores = new ArrayList<>();
        int siguienteId = 1;
        int aristasImportadas = 0;

        boolean cabeceraLeida = false;
        int numeroLinea = 0;
        String linea;

        while ((linea = reader.readLine()) != null) {
            numeroLinea++;
            String limpia = linea.replace("﻿", "").trim();   // ﻿: marca que Excel pone al inicio
            if (limpia.isEmpty()) {
                continue;
            }

            String[] columnas = limpia.split(",", -1);

            // La primera línea con texto debe ser la cabecera
            if (!cabeceraLeida) {
                cabeceraLeida = true;
                boolean cabeceraValida = columnas.length == 3
                        && columnas[0].trim().equalsIgnoreCase("origen")
                        && columnas[1].trim().equalsIgnoreCase("destino")
                        && columnas[2].trim().equalsIgnoreCase("peso");
                if (!cabeceraValida) {
                    errores.add("Línea " + numeroLinea + ": faltan las columnas requeridas. "
                            + "La primera fila debe ser: origen,destino,peso");
                    break;   // sin cabecera correcta no tiene caso revisar el resto
                }
                continue;
            }

            if (columnas.length != 3) {
                agregarError(errores, "Línea " + numeroLinea + ": registro incompleto, se esperaban 3 columnas.");
                continue;
            }

            String origen = columnas[0].trim();
            String destino = columnas[1].trim();
            String textoPeso = columnas[2].trim();

            if (origen.isEmpty() || destino.isEmpty()) {
                agregarError(errores, "Línea " + numeroLinea + ": vértice vacío.");
                continue;
            }
            if (textoPeso.isEmpty()) {
                agregarError(errores, "Línea " + numeroLinea + ": registro incompleto, falta el peso.");
                continue;
            }

            double peso;
            try {
                peso = Double.parseDouble(textoPeso);
            } catch (NumberFormatException e) {
                agregarError(errores, "Línea " + numeroLinea + ": peso no numérico ('" + textoPeso + "').");
                continue;
            }
            if (peso < 0 || Double.isNaN(peso) || Double.isInfinite(peso)) {
                agregarError(errores, "Línea " + numeroLinea + ": el peso debe ser un número no negativo.");
                continue;
            }
            if (origen.equals(destino)) {
                agregarError(errores, "Línea " + numeroLinea + ": conexión de '" + origen + "' consigo mismo (no permitida).");
                continue;
            }

            // Los vértices se identifican automáticamente por su nombre; el primero que aparece recibe el id 1
            Integer origenId = idsPorNombre.get(origen);
            if (origenId == null) {
                origenId = siguienteId++;
                idsPorNombre.put(origen, origenId);
                grafo.agregarVertice(new Vertice(origenId, origen));
            }
            Integer destinoId = idsPorNombre.get(destino);
            if (destinoId == null) {
                destinoId = siguienteId++;
                idsPorNombre.put(destino, destinoId);
                grafo.agregarVertice(new Vertice(destinoId, destino));
            }

            if (grafo.agregarArista(origenId, destinoId, peso)) {
                aristasImportadas++;
            } else {
                agregarError(errores, "Línea " + numeroLinea + ": conexión duplicada " + origen + " -> " + destino + ".");
            }
        }

        if (!cabeceraLeida) {
            errores.add("El archivo está vacío.");
        } else if (errores.isEmpty() && aristasImportadas == 0) {
            errores.add("El archivo no tiene ninguna conexión.");
        }

        return new ResultadoImportacion(grafo, grafo.getVertices().size(), aristasImportadas, errores);
    }

    private void agregarError(List<String> errores, String mensaje) {
        if (errores.size() < MAXIMO_ERRORES) {
            errores.add(mensaje);
        } else if (errores.size() == MAXIMO_ERRORES) {
            errores.add("... y más errores (se muestran solo los primeros " + MAXIMO_ERRORES + ").");
        }
    }
}
