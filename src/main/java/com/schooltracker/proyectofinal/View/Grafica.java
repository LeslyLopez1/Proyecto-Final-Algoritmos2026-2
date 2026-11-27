package com.schooltracker.proyectofinal.View;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Locale;

public class Grafica {

    // Del azul claro (grafo pequeño) al azul oscuro (grafo grande)
    private static final String[] AZULES = {"#6aabf1", "#338ced", "#1b64be", "#0e3d7e"};

    /**
     * Gráfica general del análisis empírico: una barra por cada tamaño de grafo en cada operación.
     *
     * @param operaciones nombres del eje X (importación, construcción, Dijkstra...)
     * @param tamanos     nombre de cada grafo, por ejemplo "100 vértices"
     * @param tiempos     tiempos en milisegundos: tiempos[tamaño][operación]
     */
    public static BarChart<String, Number> comparacionGeneral(String[] operaciones, String[] tamanos, double[][] tiempos) {
        CategoryAxis ejeX = new CategoryAxis();
        ejeX.setLabel("Operación medida");
        NumberAxis ejeY = new NumberAxis();
        ejeY.setLabel("Tiempo promedio (escala logarítmica)");

        BarChart<String, Number> grafica = new BarChart<>(ejeX, ejeY);
        grafica.setTitle("Comparación general del análisis empírico");

        double menor = Double.MAX_VALUE;
        double mayor = 0;

        // Una serie (un color) por cada tamaño de grafo
        for (int t = 0; t < tamanos.length; t++) {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(tamanos[t]);
            final String azul = AZULES[t % AZULES.length];

            for (int o = 0; o < operaciones.length; o++) {
                double milisegundos = tiempos[t][o];

                // JavaFX no tiene escala logarítmica: se grafica el logaritmo del tiempo en nanosegundos
                double logaritmo = Math.log10(Math.max(milisegundos * 1000000.0, 1.0));
                menor = Math.min(menor, logaritmo);
                mayor = Math.max(mayor, logaritmo);

                XYChart.Data<String, Number> barra = new XYChart.Data<>(operaciones[o], logaritmo);
                final String texto = operaciones[o] + "\n" + tamanos[t] + ": "
                        + String.format(Locale.US, "%.4f", milisegundos) + " ms";
                // La barra se crea después, por eso color y tooltip se ponen cuando ya existe su nodo
                barra.nodeProperty().addListener((obs, anterior, nodo) -> {
                    if (nodo != null) {
                        nodo.setStyle("-fx-bar-fill: " + azul + ";");
                        Tooltip.install(nodo, new Tooltip(texto));
                    }
                });
                serie.getData().add(barra);
            }
            grafica.getData().add(serie);
        }

        // Eje Y: una marca por cada potencia de 10, escrita como tiempo (10 µs, 1 ms, 100 ms...)
        if (menor == Double.MAX_VALUE) {
            menor = 3;
            mayor = 8;
        }
        ejeY.setAutoRanging(false);
        ejeY.setLowerBound(Math.floor(menor));
        ejeY.setUpperBound(Math.floor(mayor) + 1);
        ejeY.setTickUnit(1);
        ejeY.setMinorTickCount(0);
        ejeY.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number valor) {
                return tiempoLegible((int) Math.round(valor.doubleValue()));
            }

            @Override
            public Number fromString(String texto) {
                return 0;
            }
        });

        grafica.setLegendSide(Side.BOTTOM);
        grafica.setAnimated(false);
        grafica.setCategoryGap(24);

        // La leyenda se crea cuando la gráfica ya está en pantalla; ahí se le pone su azul a cada cuadrito
        grafica.sceneProperty().addListener((obs, anterior, escena) -> {
            if (escena != null) {
                Platform.runLater(() -> pintarLeyenda(grafica));
            }
        });
        return grafica;
    }

    private static void pintarLeyenda(BarChart<String, Number> grafica) {
        grafica.applyCss();
        grafica.layout();
        int numero = 0;
        for (Node simbolo : grafica.lookupAll(".chart-legend-item-symbol")) {
            simbolo.setStyle("-fx-background-color: " + AZULES[numero % AZULES.length] + ";");
            numero++;
        }
    }

    /** 10^exp nanosegundos escrito con su unidad: 3 -> "1 µs", 7 -> "10 ms", 9 -> "1 s". */
    private static String tiempoLegible(int exp) {
        String[] unidades = {"ns", "µs", "ms", "s"};
        int bloque = Math.floorDiv(exp, 3);
        if (bloque < 0 || bloque > 3) {
            return "10^" + exp + " ns";
        }
        int valor = 1;
        for (int i = 0; i < exp - 3 * bloque; i++) {
            valor = valor * 10;
        }
        return valor + " " + unidades[bloque];
    }

    /** Por si se quiere ver la gráfica en una ventana aparte. */
    public static void abrirVentana(String titulo, javafx.scene.chart.Chart grafica) {
        Stage ventana = new Stage();
        ventana.setTitle(titulo);
        ventana.setScene(new Scene(grafica, 1100, 620));
        ventana.show();
    }
}
