package com.schooltracker.proyectofinal.View.Seleccion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

public class SelectorCsvComboBox extends ComboBox<String> {

    private static final String CARPETA_RECURSOS = "/CSV/";
    private static final List<String> CSV_RESPALDO = List.of(
            "grafo_peque.csv",
            "grafo_mediano.csv",
            "grafo_grande.csv",
            "grafo_muy_grande.csv"
    );

    public SelectorCsvComboBox() {
        List<String> archivos = descubrirArchivosCsv();
        this.setItems(FXCollections.observableArrayList(archivos));
        this.getStyleClass().add("combo-csv");
        this.setPromptText("Seleccionar CSV");
        this.setMinWidth(185.0);
        this.setPrefWidth(185.0);
    }

    public void recargarArchivos() {
        String seleccionPrevia = this.getValue();
        List<String> archivos = descubrirArchivosCsv();
        this.setItems(FXCollections.observableArrayList(archivos));
        if (seleccionPrevia != null && archivos.contains(seleccionPrevia)) {
            this.setValue(seleccionPrevia);
        }
    }

    private List<String> descubrirArchivosCsv() {
        List<String> encontrados = new ArrayList<>();

        URL urlCarpeta = SelectorCsvComboBox.class.getResource(CARPETA_RECURSOS);
        if (urlCarpeta != null) {
            if ("file".equalsIgnoreCase(urlCarpeta.getProtocol())) {
                encontrados.addAll(leerDesdeDirectorioEnDisco(urlCarpeta));
            }
        }

        if (encontrados.isEmpty()) {
            encontrados.addAll(leerDesdeStreamDeCarpeta());
        }

        if (encontrados.isEmpty()) {
            encontrados.addAll(CSV_RESPALDO);
        }

        return encontrados;
    }

    private List<String> leerDesdeDirectorioEnDisco(URL urlCarpeta) {
        List<String> nombres = new ArrayList<>();
        try {
            URI uri = urlCarpeta.toURI();
            Path ruta = Paths.get(uri);
            if (Files.isDirectory(ruta)) {
                try (Stream<Path> stream = Files.list(ruta)) {
                    stream.filter(Files::isRegularFile)
                            .map(path -> path.getFileName().toString())
                            .filter(nombre -> nombre.toLowerCase().endsWith(".csv"))
                            .sorted(Comparator.naturalOrder())
                            .forEach(nombres::add);
                }
            }
        } catch (URISyntaxException | IOException ignored) {
            // Si falla el acceso por sistema de archivos, se usará la siguiente estrategia
        }
        return nombres;
    }

    private List<String> leerDesdeStreamDeCarpeta() {
        List<String> nombres = new ArrayList<>();
        try (InputStream is = SelectorCsvComboBox.class.getResourceAsStream(CARPETA_RECURSOS)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        String limpia = linea.trim();
                        if (limpia.toLowerCase().endsWith(".csv")) {
                            nombres.add(limpia);
                        }
                    }
                }
            }
        } catch (IOException ignored) {
            // Si el runtime no permite listar el directorio vía stream, se recurre al respaldo
        }
        nombres.sort(Comparator.naturalOrder());
        return nombres;
    }

    public String getArchivoSeleccionado() {
        return this.getValue();
    }

    public String getRutaRecursoSeleccionado() {
        String archivo = this.getValue();
        if (archivo == null || archivo.isBlank()) {
            return null;
        }
        return CARPETA_RECURSOS + archivo;
    }

    public void setOnSeleccionCambio(EventHandler<ActionEvent> handler) {
        this.setOnAction(handler);
    }
}