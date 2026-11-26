package com.schooltracker.proyectofinal.View;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.net.URL;

public class MainView extends BorderPane {
    public MainView() {
        this.getStyleClass().add("main-screen");
        applyStylesheet();
        this.setTop(buildNavBar());
        this.setLeft(buildSideBar());
    }

    private void applyStylesheet() {
        URL cssUrl = getClass().getResource("/styles.css");
        if (cssUrl != null) {
            this.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    private Node buildNavBar() {
        NavBar navBar = new NavBar("Proyecto Final");
        this.setMaxWidth(Double.MAX_VALUE);
        return navBar;
    }

    private Node buildSideBar() {
        SideBar sideBar = new SideBar();
        return sideBar;
    }


}