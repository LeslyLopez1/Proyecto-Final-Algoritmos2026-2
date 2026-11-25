package com.schooltracker.proyectofinal.View;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {
    private final QuickButton prueba;

    public SideBar() {
        double sidebarWidth = 200.0;
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.getStyleClass().add("side-bar");

        this.prueba = new QuickButton("Boton de prueba");

        this.getChildren().addAll(prueba);

    }
}