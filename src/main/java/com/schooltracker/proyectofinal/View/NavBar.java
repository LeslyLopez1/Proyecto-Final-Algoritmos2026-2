package com.schooltracker.proyectofinal.View;

import com.schooltracker.proyectofinal.View.Botones.BotonSimple;
import com.schooltracker.proyectofinal.View.Botones.BotonToggle;
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

    private final Label titleLabel;
    private final BotonToggle tipoGrafoToggle;
    private final TextField searchField;
    private final HBox actionsBox;
    private final BotonSimple weekView;
    private final BotonSimple dayView;

    private boolean grafoDirigido;

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

        this.searchField = new TextField();
        this.searchField.setPromptText("Search...");
        this.searchField.getStyleClass().add("nav-search");
        this.searchField.setPrefWidth(220);

        this.weekView = new BotonSimple("Week");
        this.dayView = new BotonSimple("Day");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        this.actionsBox = new HBox(10);
        this.actionsBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(
                titleLabel,
                tipoGrafoToggle,
                dayView,
                weekView,
                spacer,
                actionsBox
        );
    }

    private void actualizarEstadoTipoGrafo(boolean esDirigido) {
        this.grafoDirigido = esDirigido;
        if (this.tipoGrafoToggle.isActive() != esDirigido) {
            this.tipoGrafoToggle.setActive(esDirigido);
        }
        this.tipoGrafoToggle.setText(esDirigido ? TEXTO_DIRIGIDO : TEXTO_NO_DIRIGIDO);
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

    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    public void addAction(Button button) {
        button.getStyleClass().add("nav-action-button");
        this.actionsBox.getChildren().add(button);
    }

    public void onSearch(java.util.function.Consumer<String> searchConsumer) {
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchConsumer.accept(newValue);
        });
    }
}