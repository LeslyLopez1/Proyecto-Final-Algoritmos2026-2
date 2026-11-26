package com.schooltracker.proyectofinal.View;

import java.net.URL;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final NavBar navBar;
    private final SideBar sideBar;
    private GrafoView grafoView;

    public MainView() {
        this.getStyleClass().add("main-screen");
        this.setMaxWidth(Double.MAX_VALUE);
        applyStylesheet();

        this.navBar = new NavBar("Proyecto Final");
        this.sideBar = new SideBar();
        this.grafoView = new GrafoView();

        this.setTop(this.navBar);
        this.setLeft(this.sideBar);
        this.setCenter(this.grafoView);
    }

    private void applyStylesheet() {
        URL cssUrl = getClass().getResource("/styles.css");
        if (cssUrl != null) {
            this.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    public void actualizarGrafoView(GrafoView nuevaGrafoView) {
        if (nuevaGrafoView == null) {
            throw new IllegalArgumentException("GrafoView no puede ser nula.");
        }
        this.grafoView = nuevaGrafoView;
        this.setCenter(this.grafoView);
    }

    public NavBar getNavBar() {
        return navBar;
    }

    public SideBar getSideBar() {
        return sideBar;
    }

    public GrafoView getGrafoView() {
        return grafoView;
    }
}