package com.schooltracker.proyectofinal.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    // Medidas para grafos pequeños (hasta VERTICES_TAMANO_NORMAL vértices); con más se hacen más chicos los nodos
    private static final double RADIO_NODO_BASE = 34.0;
    private static final double RADIO_NODO_MINIMO = 13.0;
    private static final double ANCHO_LIENZO_BASE = 900.0;
    private static final double ALTO_LIENZO_BASE = 600.0;
    private static final int VERTICES_TAMANO_NORMAL = 16;
    // Con más conexiones que esto, los pesos se ocultan porque no se alcanzarían a leer
    private static final int MAXIMO_CONEXIONES_CON_PESO = 80;
    private static final double TAMANO_FLECHA = 10.0;
    private static final double ANGULO_FLECHA = Math.toRadians(28.0);

    private final Pane lienzo;
    private final ScrollPane scrollPane;
    private final Map<String, Point2D> posicionesPorEtiqueta;
    private final Set<String> paresNoDirigidosDibujados;

    private boolean dirigido;
    private double radioNodo = RADIO_NODO_BASE;
    private double anchoLienzo = ANCHO_LIENZO_BASE;
    private double altoLienzo = ALTO_LIENZO_BASE;
    private boolean mostrarPesos = true;

    public GrafoView() {
        this(true);
    }

    public GrafoView(boolean dirigido) {
        this.dirigido = dirigido;
        this.posicionesPorEtiqueta = new HashMap<>();
        this.paresNoDirigidosDibujados = new HashSet<>();

        this.lienzo = new Pane();
        this.lienzo.setPrefSize(anchoLienzo, altoLienzo);
        this.lienzo.setMinSize(anchoLienzo, altoLienzo);
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

    /** Muestra un texto en lugar del dibujo (por ejemplo, cuando el grafo es demasiado grande para dibujarlo). */
    public void mostrarAviso(String texto) {
        this.lienzo.getChildren().clear();
        this.paresNoDirigidosDibujados.clear();
        Label aviso = new Label(texto);
        aviso.setWrapText(true);
        aviso.setMaxWidth(anchoLienzo - 120);
        aviso.setStyle("-fx-font-size: 15px; -fx-text-fill: #444444;");
        aviso.setLayoutX(60);
        aviso.setLayoutY(60);
        this.lienzo.getChildren().add(aviso);
    }

    /** Texto pequeño en la esquina del dibujo (por ejemplo, "Mostrando 50 de 500 vértices"). */
    public void setNota(String texto) {
        Label nota = new Label(texto);
        nota.setStyle("-fx-font-size: 13px; -fx-text-fill: #444444; -fx-background-color: rgba(255,255,255,0.85); -fx-padding: 4 8 4 8;");
        nota.setLayoutX(10);
        nota.setLayoutY(10);
        this.lienzo.getChildren().add(nota);
    }

    public void establecerDatos(List<String> etiquetasVertices, List<String> conexiones) {
        this.lienzo.getChildren().clear();
        this.paresNoDirigidosDibujados.clear();

        List<String> vertices = (etiquetasVertices == null) ? List.of() : etiquetasVertices;
        List<String> aristas = (conexiones == null) ? List.of() : conexiones;

        if (vertices.isEmpty()) {
            this.posicionesPorEtiqueta.clear();
            return;
        }

        ajustarTamanos(vertices.size(), aristas.size());
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

    /** Con muchos vértices los nodos se hacen más chicos y el círculo más grande para que no se encimen. */
    private void ajustarTamanos(int totalVertices, int totalConexiones) {
        if (totalVertices <= VERTICES_TAMANO_NORMAL) {
            radioNodo = RADIO_NODO_BASE;
            anchoLienzo = ANCHO_LIENZO_BASE;
            altoLienzo = ALTO_LIENZO_BASE;
        } else {
            radioNodo = Math.max(RADIO_NODO_MINIMO, RADIO_NODO_BASE - (totalVertices - VERTICES_TAMANO_NORMAL) * 0.6);
            double lado = 2.0 * (radioCirculo(totalVertices) + radioNodo + 70.0);
            anchoLienzo = Math.max(ANCHO_LIENZO_BASE, lado);
            altoLienzo = Math.max(ALTO_LIENZO_BASE, lado);
        }
        mostrarPesos = totalConexiones <= MAXIMO_CONEXIONES_CON_PESO;
        this.lienzo.setPrefSize(anchoLienzo, altoLienzo);
        this.lienzo.setMinSize(anchoLienzo, altoLienzo);
    }

    /** Radio del círculo donde se colocan los vértices: el de siempre, o más grande si no caben. */
    private double radioCirculo(int totalVertices) {
        double radioBase = Math.max(120.0, Math.min(ANCHO_LIENZO_BASE, ALTO_LIENZO_BASE) / 2.0 - 90.0);
        if (totalVertices <= VERTICES_TAMANO_NORMAL) {
            return radioBase;
        }
        double radioNecesario = totalVertices * (2.0 * radioNodo + 10.0) / (2.0 * Math.PI);
        return Math.max(radioBase, radioNecesario);
    }

    private void calcularPosiciones(List<String> vertices) {
        int total = vertices.size();
        double centroX = anchoLienzo / 2.0;
        double centroY = altoLienzo / 2.0;
        double radioDistribucion = radioCirculo(total);

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

        if (!this.dirigido) {
            String clavePar = crearClaveParNoDirigido(origen, destino);
            if (!this.paresNoDirigidosDibujados.add(clavePar)) {
                return;
            }
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

        double inicioX = pOrigen.getX() + ux * radioNodo;
        double inicioY = pOrigen.getY() + uy * radioNodo;
        double finX = pDestino.getX() - ux * radioNodo;
        double finY = pDestino.getY() - uy * radioNodo;

        Line linea = new Line(inicioX, inicioY, finX, finY);
        linea.getStyleClass().add("grafo-arista");
        this.lienzo.getChildren().add(linea);

        if (this.dirigido) {
            Polygon flecha = crearPuntaFlecha(finX, finY, ux, uy);
            this.lienzo.getChildren().add(flecha);
        }

        if (mostrarPesos && !peso.isEmpty()) {
            Label etiquetaPeso = new Label(peso);
            etiquetaPeso.getStyleClass().add("grafo-peso");

            double offset = this.dirigido ? 16.0 : 12.0;
            double medioX = (inicioX + finX) / 2.0 - uy * offset;
            double medioY = (inicioY + finY) / 2.0 + ux * offset;

            etiquetaPeso.applyCss();
            double anchoPeso = etiquetaPeso.prefWidth(-1);
            double altoPeso = etiquetaPeso.prefHeight(-1);

            etiquetaPeso.setLayoutX(medioX - anchoPeso / 2.0);
            etiquetaPeso.setLayoutY(medioY - altoPeso / 2.0);
            this.lienzo.getChildren().add(etiquetaPeso);
        }
    }

    private String crearClaveParNoDirigido(String a, String b) {
        return (a.compareTo(b) <= 0) ? (a + "|" + b) : (b + "|" + a);
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
        Circle circulo = new Circle(radioNodo);
        circulo.getStyleClass().add("grafo-nodo-circulo");

        Label texto = new Label(etiqueta);
        texto.setWrapText(false);
        texto.setAlignment(Pos.CENTER);
        texto.getStyleClass().add("grafo-nodo-etiqueta");

        if (radioNodo < 24.0) {
            texto.setStyle("-fx-font-size: 8px;");   // nodos chicos: letra chica
        } else if (etiqueta != null && etiqueta.length() >= 10) {
            texto.setStyle("-fx-font-size: 9.5px;");
        }

        StackPane nodo = new StackPane(circulo, texto);
        nodo.setPrefSize(radioNodo * 2, radioNodo * 2);
        nodo.setLayoutX(centro.getX() - radioNodo);
        nodo.setLayoutY(centro.getY() - radioNodo);
        nodo.getStyleClass().add("grafo-nodo");

        this.lienzo.getChildren().add(nodo);
    }
}