package com.schooltracker.proyectofinal.View.Seleccion;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

public class SelectorCsvComboBox extends ComboBox<String> {

    private static final String CARPETA_RECURSOS = "/CSV/";
    private static final List<String> ARCHIVOS_CSV = List.of(
            "grafo_peque.csv",
            "grafo_mediano.csv",
            "grafo_grande.csv",
            "grafo_muy_grande.csv"
    );

    public SelectorCsvComboBox() {
        this.setItems(FXCollections.observableArrayList(ARCHIVOS_CSV));
        this.getStyleClass().add("combo-csv");
        this.setPromptText("Seleccionar CSV");
        this.setMinWidth(185.0);
        this.setPrefWidth(185.0);
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