package com.schooltracker.proyectofinal.View;

import javafx.scene.control.Button;

public abstract class QuickButton extends Button {
    public QuickButton(String icon) {
        super(icon);
        this.getStyleClass().add("quick-button");
    }
    protected abstract void addAction();
}