package com.schooltracker.proyectofinal.View.Botones;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;

public class BotonToggle extends ToggleButton {
    public BotonToggle(String text) {
        this.setText(text);
        this.getStyleClass().add("boton-toggle");
    }

    public void setFixedSize(double width, double height) {
        this.setMinSize(width, height);
        this.setPrefSize(width, height);
        this.setMaxSize(width, height);
    }

    public boolean isActive() {
        return this.isSelected();
    }

    public void setActive(boolean active) {
        this.setSelected(active);
    }

    public EventHandler<ActionEvent> getAction() {
        return this.getOnAction();
    }

    public void setAction(EventHandler<ActionEvent> action) {
        this.setOnAction(action);
    }
}
