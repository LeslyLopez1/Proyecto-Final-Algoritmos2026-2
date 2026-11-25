package com.schooltracker.proyectofinal.View;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class QuickButton extends Button {
    public QuickButton(String icon) {
        super(icon);
        this.getStyleClass().add("quick-button");
    }

    public EventHandler<ActionEvent> getAction() {
        return this.getOnAction();
    }

    public void setAction(EventHandler<ActionEvent> action) {
        this.setOnAction(action);
    }
}