package com.schooltracker.proyectofinal.View;

import com.schooltracker.proyectofinal.Model.AnalisisEmpirico;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//vista para la seccion de analisis
public class AnalisisView extends VBox {

    private final StackPane contenido;
    private final Button botonRepetir;
    private boolean medido;
    private boolean midiendo;

    public AnalisisView() {
        this.getStyleClass().add("analisis-view");
        this.setSpacing(10);

        Label titulo = new Label("Análisis empírico");
        titulo.getStyleClass().add("analisis-titulo");

        this.botonRepetir = new Button("Volver a medir");
        this.botonRepetir.setOnAction(e -> medir());

        HBox encabezado = new HBox(15, titulo, botonRepetir);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        this.contenido = new StackPane();
        VBox.setVgrow(this.contenido, Priority.ALWAYS);

        this.getChildren().addAll(encabezado, contenido);
    }

    public void mostrar() {
        if (!medido && !midiendo) {
            medir();
        }
    }

    private void medir() {
        if (midiendo) {
            return;
        }
        midiendo = true;
        botonRepetir.setDisable(true);

        VBox espera = new VBox(10, new ProgressIndicator(), new Label("Midiendo tiempos con los grafos de prueba..."));
        espera.setAlignment(Pos.CENTER);
        contenido.getChildren().setAll(espera);

        Task<double[][]> tarea = new Task<double[][]>() {
            @Override
            protected double[][] call() throws Exception {
                return AnalisisEmpirico.ejecutar();
            }
        };

        tarea.setOnSucceeded(e -> {
            String[] tamanos = new String[AnalisisEmpirico.ARCHIVOS.length];
            for (int i = 0; i < tamanos.length; i++) {
                String archivo = AnalisisEmpirico.ARCHIVOS[i];                
                tamanos[i] = archivo.substring(archivo.indexOf('_') + 1, archivo.indexOf('v')) + " vértices";
            }
            contenido.getChildren().setAll(
                    Grafica.comparacionGeneral(AnalisisEmpirico.OPERACIONES, tamanos, tarea.getValue()));
            medido = true;
            midiendo = false;
            botonRepetir.setDisable(false);
        });

        tarea.setOnFailed(e -> {
            Label error = new Label("No se pudo hacer el análisis: " + tarea.getException().getMessage());
            contenido.getChildren().setAll(error);
            midiendo = false;
            botonRepetir.setDisable(false);
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }
}
