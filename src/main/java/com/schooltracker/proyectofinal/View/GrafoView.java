package com.schooltracker.proyectofinal.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class GrafoView extends VBox {

    private static final double RADIO_NODO = 28.0;
    private static final double ANCHO_LIENZO = 900.0;
    private static final double ALTO_LIENZO = 600.0;
    private static final double TAMANO_FLECHA = 10.0;
    private static final double ANGULO_FLECHA = Math.toRadians(28.0);

    private final Pane lienzo;
    private final ScrollPane scrollPane;
    private final Map<String, Point2D> posicionesPorEtiqueta;

    private boolean dirigido;

    public GrafoView() {
        this(true);
    }

    public GrafoView(boolean dirigido) {
        this.dirigido = dirigido;
        this.posicionesPorEtiqueta = new HashMap<>();

        this.lienzo = new Pane();
        this.lienzo.setPrefSize(ANCHO_LIENZO, ALTO_LIENZO);
        this.lienzo.setMinSize(ANCHO_LIENZO, ALTO_LIENZO);
        this.lienzo.getStyleClass().add("grafo-lienzo");

        this.scrollPane = new ScrollPane(this.lienzo);
        this.scrollPane.setPannable(true);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.getStyleClass().add("grafo-scroll");
        VBox.setVgrow(this.scrollPane, Priority.ALWAYS);

        this.getStyleClass().add("grafo-view");
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.getChildren().add(this.scrollPane);
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public void establecerDatos(List<String> etiquetasVertices, List<String> conexiones) {
        this.lienzo.getChildren().clear();

        List<String> vertices = (etiquetasVertices == null) ? List.of() : etiquetasVertices;
        List<String> aristas = (conexiones == null) ? List.of() : conexiones;

        if (vertices.isEmpty()) {
            this.posicionesPorEtiqueta.clear();
            return;
        }

        calcularPosiciones(vertices);

        for (String descripcionConexion : aristas) {
            dibujarConexionDesdeTexto(descripcionConexion);
        }

        for (String etiqueta : vertices) {
            Point2D centro = this.posicionesPorEtiqueta.get(etiqueta);
            if (centro != null) {
                dibujarNodo(etiqueta, centro);
            }
        }
    }

    private void calcularPosiciones(List<String> vertices) {
        int total = vertices.size();
        double centroX = ANCHO_LIENZO / 2.0;
        double centroY = ALTO_LIENZO / 2.0;
        double radioDistribucion = Math.max(120.0, Math.min(centroX, centroY) - 90.0);

        List<String> vigentes = new ArrayList<>(vertices);
        this.posicionesPorEtiqueta.keySet().retainAll(vigentes);

        for (int i = 0; i < total; i++) {
            String etiqueta = vertices.get(i);
            if (!this.posicionesPorEtiqueta.containsKey(etiqueta)) {
                double angulo = (2.0 * Math.PI * i) / total;
                double x = centroX + radioDistribucion * Math.cos(angulo);
                double y = centroY + radioDistribucion * Math.sin(angulo);
                this.posicionesPorEtiqueta.put(etiqueta, new Point2D(x, y));
            }
        }
    }

    // Espera el formato armado en Controlador: "Origen -> Destino (Peso)"
    private void dibujarConexionDesdeTexto(String texto) {
        if (texto == null || !texto.contains(" -> ")) {
            return;
        }

        String[] partesFlecha = texto.split(" -> ", 2);
        String origen = partesFlecha[0].trim();
        String resto = partesFlecha[1].trim();

        String destino = resto;
        String peso = "";
        int indiceParentesis = resto.lastIndexOf(" (");
        if (indiceParentesis != -1 && resto.endsWith(")")) {
            destino = resto.substring(0, indiceParentesis).trim();
            peso = resto.substring(indiceParentesis + 2, resto.length() - 1).trim();
        }

        Point2D pOrigen = this.posicionesPorEtiqueta.get(origen);
        Point2D pDestino = this.posicionesPorEtiqueta.get(destino);
        if (pOrigen == null || pDestino == null) {
            return;
        }

        double dx = pDestino.getX() - pOrigen.getX();
        double dy = pDestino.getY() - pOrigen.getY();
        double distancia = Math.hypot(dx, dy);
        if (distancia == 0) {
            return;
        }

        double ux = dx / distancia;
        double uy = dy / distancia;

        double inicioX = pOrigen.getX() + ux * RADIO_NODO;
        double inicioY = pOrigen.getY() + uy * RADIO_NODO;
        double finX = pDestino.getX() - ux * RADIO_NODO;
        double finY = pDestino.getY() - uy * RADIO_NODO;

        Line linea = new Line(inicioX, inicioY, finX, finY);
        linea.getStyleClass().add("grafo-arista");
        this.lienzo.getChildren().add(linea);

        if (this.dirigido) {
            Polygon flecha = crearPuntaFlecha(finX, finY, ux, uy);
            this.lienzo.getChildren().add(flecha);
        }

        if (!peso.isEmpty()) {
            Label etiquetaPeso = new Label(peso);
            etiquetaPeso.getStyleClass().add("grafo-peso");

            double medioX = (inicioX + finX) / 2.0 - uy * 12.0;
            double medioY = (inicioY + finY) / 2.0 + ux * 12.0;
            etiquetaPeso.setLayoutX(medioX - 16.0);
            etiquetaPeso.setLayoutY(medioY - 10.0);
            this.lienzo.getChildren().add(etiquetaPeso);
        }
    }

    private Polygon crearPuntaFlecha(double puntaX, double puntaY, double ux, double uy) {
        double angulo = Math.atan2(uy, ux);
        double x1 = puntaX - TAMANO_FLECHA * Math.cos(angulo - ANGULO_FLECHA);
        double y1 = puntaY - TAMANO_FLECHA * Math.sin(angulo - ANGULO_FLECHA);
        double x2 = puntaX - TAMANO_FLECHA * Math.cos(angulo + ANGULO_FLECHA);
        double y2 = puntaY - TAMANO_FLECHA * Math.sin(angulo + ANGULO_FLECHA);

        Polygon triangulo = new Polygon(puntaX, puntaY, x1, y1, x2, y2);
        triangulo.getStyleClass().add("grafo-flecha");
        return triangulo;
    }

    private void dibujarNodo(String etiqueta, Point2D centro) {
        Circle circulo = new Circle(RADIO_NODO);
        circulo.getStyleClass().add("grafo-nodo-circulo");

        Label texto = new Label(etiqueta);
        texto.setWrapText(true);
        texto.setAlignment(Pos.CENTER);
        texto.getStyleClass().add("grafo-nodo-etiqueta");

        StackPane nodo = new StackPane(circulo, texto);
        nodo.setPrefSize(RADIO_NODO * 2, RADIO_NODO * 2);
        nodo.setLayoutX(centro.getX() - RADIO_NODO);
        nodo.setLayoutY(centro.getY() - RADIO_NODO);
        nodo.getStyleClass().add("grafo-nodo");

        this.lienzo.getChildren().add(nodo);
    }
}