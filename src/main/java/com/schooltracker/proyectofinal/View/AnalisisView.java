package com.schooltracker.proyectofinal.View;

import com.schooltracker.proyectofinal.Model.AnalisisEmpirico;
import com.schooltracker.proyectofinal.Model.ResultadoAnalisis;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Locale;

/** Vista que se muestra cuando el botón de la barra superior dice "Analisis": gráfica y tabla de tiempos. */
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

    /** Mide solo la primera vez que se entra; después se queda lo que ya se calculó. */
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

        // La medición tarda unos segundos: se hace en otro hilo para que la ventana no se congele
        Task<ResultadoAnalisis> tarea = new Task<ResultadoAnalisis>() {
            @Override
            protected ResultadoAnalisis call() throws Exception {
                return AnalisisEmpirico.ejecutar();
            }
        };

        tarea.setOnSucceeded(e -> {
            ResultadoAnalisis resultado = tarea.getValue();
            VBox grafica = new VBox(8,
                    Grafica.comparacionGeneral(AnalisisEmpirico.OPERACIONES, resultado.getEtiquetas(), resultado.getTiempos()),
                    crearTabla(resultado));
            VBox.setVgrow(grafica.getChildren().get(0), Priority.ALWAYS);
            contenido.getChildren().setAll(grafica);
            medido = true;
            midiendo = false;
            botonRepetir.setDisable(false);
        });

        tarea.setOnFailed(e -> {
            contenido.getChildren().setAll(new Label("No se pudo hacer el análisis: " + tarea.getException().getMessage()));
            midiendo = false;
            botonRepetir.setDisable(false);
        });

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Tabla con los mismos tiempos de la gráfica, en milisegundos (sirve para el reporte). */
    private GridPane crearTabla(ResultadoAnalisis resultado) {
        GridPane tabla = new GridPane();
        tabla.setHgap(18);
        tabla.setVgap(4);
        tabla.getStyleClass().add("analisis-tabla");

        tabla.add(crearCelda("Tiempo promedio (ms)", true), 0, 0);
        for (int o = 0; o < AnalisisEmpirico.OPERACIONES.length; o++) {
            tabla.add(crearCelda(AnalisisEmpirico.OPERACIONES[o], true), o + 1, 0);
        }
        for (int t = 0; t < resultado.getEtiquetas().length; t++) {
            tabla.add(crearCelda(resultado.getEtiquetas()[t], false), 0, t + 1);
            for (int o = 0; o < AnalisisEmpirico.OPERACIONES.length; o++) {
                tabla.add(crearCelda(String.format(Locale.US, "%.4f", resultado.getTiempos()[t][o]), false), o + 1, t + 1);
            }
        }
        return tabla;
    }

    private Label crearCelda(String texto, boolean encabezado) {
        Label celda = new Label(texto);
        celda.setStyle(encabezado ? "-fx-font-weight: bold;" : "-fx-font-size: 12px;");
        return celda;
    }
}
