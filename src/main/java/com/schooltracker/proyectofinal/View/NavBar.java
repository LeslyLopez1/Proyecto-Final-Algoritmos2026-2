package com.schooltracker.proyectofinal.View;

import com.schooltracker.proyectofinal.View.Botones.BotonToggle;
import com.schooltracker.proyectofinal.View.Seleccion.SelectorCsvComboBox;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBar extends HBox {

    private static final String TEXTO_DIRIGIDO = "Dirigido";
    private static final String TEXTO_NO_DIRIGIDO = "No Dirigido";
    private static final double ANCHO_TIPO_GRAFO = 120.0;
    private static final double ALTO_TIPO_GRAFO = 30.0;

    private static final String TEXTO_GRAFO = "Grafo";
    private static final String TEXTO_ANALISIS = "Analisis";
    private static final double ANCHO_VISTA = 110.0;
    private static final double ALTO_VISTA = 30.0;

    private final Label titleLabel;
    private final BotonToggle tipoGrafoToggle;
    private final BotonToggle grafoAnalisis;
    private final HBox actionsBox;
    private final SelectorCsvComboBox selectorCsv;

    private boolean grafoDirigido;
    private boolean vistaGrafoActiva;

    public NavBar(String appTitle) {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.setMaxWidth(Double.MAX_VALUE);
        this.getStyleClass().add("nav-bar");

        this.titleLabel = new Label(appTitle);
        this.titleLabel.getStyleClass().add("nav-title");

        this.tipoGrafoToggle = new BotonToggle(TEXTO_DIRIGIDO);
        this.tipoGrafoToggle.setFixedSize(ANCHO_TIPO_GRAFO, ALTO_TIPO_GRAFO);
        this.tipoGrafoToggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            actualizarEstadoTipoGrafo(isSelected);
        });
        actualizarEstadoTipoGrafo(true);

        this.grafoAnalisis = new BotonToggle(TEXTO_GRAFO);
        this.grafoAnalisis.setFixedSize(ANCHO_VISTA, ALTO_VISTA);
        this.grafoAnalisis.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            actualizarEstadoVista(isSelected);
        });
        actualizarEstadoVista(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.actionsBox = new HBox(10);
        this.actionsBox.setAlignment(Pos.CENTER);

        this.selectorCsv = new SelectorCsvComboBox();

        this.getChildren().addAll(
                titleLabel,
                tipoGrafoToggle,
                selectorCsv,
                spacer,
                actionsBox,
                grafoAnalisis
        );
    }

    private void actualizarEstadoTipoGrafo(boolean esDirigido) {
        this.grafoDirigido = esDirigido;
        if (this.tipoGrafoToggle.isActive() != esDirigido) {
            this.tipoGrafoToggle.setActive(esDirigido);
        }
        this.tipoGrafoToggle.setText(esDirigido ? TEXTO_DIRIGIDO : TEXTO_NO_DIRIGIDO);
    }

    private void actualizarEstadoVista(boolean esVistaGrafo) {
        this.vistaGrafoActiva = esVistaGrafo;
        if (this.grafoAnalisis.isActive() != esVistaGrafo) {
            this.grafoAnalisis.setActive(esVistaGrafo);
        }
        this.grafoAnalisis.setText(esVistaGrafo ? TEXTO_GRAFO : TEXTO_ANALISIS);
    }

    public boolean isGrafoDirigido() {
        return this.grafoDirigido;
    }

    public void setGrafoDirigido(boolean dirigido) {
        actualizarEstadoTipoGrafo(dirigido);
    }

    public void onTipoGrafoChange(ChangeListener<Boolean> listener) {
        this.tipoGrafoToggle.selectedProperty().addListener(listener);
    }

    public boolean isVistaGrafoActiva() {
        return this.vistaGrafoActiva;
    }

    public void setVistaGrafoActiva(boolean esVistaGrafo) {
        actualizarEstadoVista(esVistaGrafo);
    }

    public void onVistaChange(ChangeListener<Boolean> listener) {
        this.grafoAnalisis.selectedProperty().addListener(listener);
    }

    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    public void addAction(Button button) {
        button.getStyleClass().add("nav-action-button");
        this.actionsBox.getChildren().add(button);
    }

    public String getArchivoCsvSeleccionado() {
        return this.selectorCsv.getArchivoSeleccionado();
    }

    public String getRutaCsvSeleccionado() {
        return this.selectorCsv.getRutaRecursoSeleccionado();
    }

    public void onSeleccionCsv(javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        this.selectorCsv.setOnSeleccionCambio(handler);
    }
}