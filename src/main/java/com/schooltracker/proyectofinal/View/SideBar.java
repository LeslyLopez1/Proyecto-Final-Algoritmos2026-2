package com.schooltracker.proyectofinal.View;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {
    //    private final Label label;
    private final QuickButton open;
    private final QuickButton close;

    public SideBar() {
        // VBox setup
        double sidebarWidth = 200.0;
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.getStyleClass().add("side-bar");

        this.open = new QuickButton("Open");
        this.close = new QuickButton("Close");

        this.getChildren().addAll(open, close);

    }
}