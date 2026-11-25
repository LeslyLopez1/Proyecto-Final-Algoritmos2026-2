package com.schooltracker.proyectofinal.View;

import com.schooltracker.proyectofinal.View.Botones.BotonSimple;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {
    private final BotonSimple prueba;

    public SideBar() {
        double sidebarWidth = 200.0;
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.getStyleClass().add("side-bar");

        this.prueba = new BotonSimple("Boton de prueba");

        this.getChildren().addAll(prueba);

    }
}