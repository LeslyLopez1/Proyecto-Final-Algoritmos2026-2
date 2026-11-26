package com.schooltracker.proyectofinal.View;

import com.schooltracker.proyectofinal.View.Botones.BotonSimple;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {

    private static final double SIDEBAR_WIDTH = 250.0;

    // Controles de Vértices
    private final TextField verticeField;
    private final TextField nuevoVerticeField;
    private final BotonSimple btnAgregarVertice;
    private final BotonSimple btnModificarVertice;
    private final BotonSimple btnEliminarVertice;

    // Controles de Aristas
    private final TextField origenField;
    private final TextField destinoField;
    private final TextField pesoField;
    private final BotonSimple btnAgregarArista;
    private final BotonSimple btnModificarArista;
    private final BotonSimple btnEliminarArista;

    // Controles de Búsqueda (BFS / DFS)
    private final ComboBox<Integer> verticeInicialCombo;
    private final BotonSimple btnEjecutarBfs;
    private final BotonSimple btnEjecutarDfs;
    private final Label recorridoLabel;

    // Controles de Ruta Óptima (Dijkstra)
    private final ComboBox<Integer> origenDijkstraCombo;
    private final ComboBox<Integer> destinoDijkstraCombo;
    private final BotonSimple btnCalcularDijkstra;
    private final Label resultadoDijkstraLabel;

    // Controles de Ordenamiento (QuickSort / Shell Sort)
    private final ComboBox<String> criterioOrdenCombo;
    private final BotonSimple btnQuickSort;
    private final BotonSimple btnShellSort;
    private final Label resultadoOrdenLabel;

    // Mensaje de validación / estado
    private final Label mensajeLabel;

    public SideBar() {
        this.setMinWidth(SIDEBAR_WIDTH);
        this.setPrefWidth(SIDEBAR_WIDTH);
        this.setMaxWidth(SIDEBAR_WIDTH);
        this.setMaxHeight(Double.MAX_VALUE);
        this.getStyleClass().add("side-bar");

        VBox contenido = new VBox(10);
        contenido.setPadding(new Insets(10, 14, 16, 14));
        contenido.setAlignment(Pos.TOP_LEFT);
        contenido.setFillWidth(true);

        // --- Sección 1: Vértices ---
        Label tituloVertices = crearTituloSeccion("Vértices");
        this.verticeField = crearCampoTexto("ID vértice (entero)");
        this.nuevoVerticeField = crearCampoTexto("Nuevo ID (para modificar)");

        this.btnAgregarVertice = new BotonSimple("Agregar");
        this.btnModificarVertice = new BotonSimple("Modificar");
        this.btnEliminarVertice = new BotonSimple("Eliminar");

        HBox filaBotonesVertice = crearFilaBotones(
                btnAgregarVertice,
                btnModificarVertice,
                btnEliminarVertice
        );

        // --- Sección 2: Aristas ---
        Label tituloAristas = crearTituloSeccion("Aristas");
        this.origenField = crearCampoTexto("Origen (entero)");
        this.destinoField = crearCampoTexto("Destino (entero)");
        this.pesoField = crearCampoTexto("Peso (>= 0)");

        this.btnAgregarArista = new BotonSimple("Agregar");
        this.btnModificarArista = new BotonSimple("Modificar");
        this.btnEliminarArista = new BotonSimple("Eliminar");

        HBox filaBotonesArista = crearFilaBotones(
                btnAgregarArista,
                btnModificarArista,
                btnEliminarArista
        );
        contenido.setPadding(new Insets(10, 16, 16, 14));
        // --- Sección 3a: Búsqueda (BFS / DFS) ---
        Label tituloBusqueda = crearTituloSeccion("Búsqueda (BFS / DFS)");
        Label etiquetaInicial = crearSubetiqueta("Vértice inicial");

        this.verticeInicialCombo = crearComboEnteros("Seleccionar vértice");
        this.btnEjecutarBfs = new BotonSimple("BFS");
        this.btnEjecutarDfs = new BotonSimple("DFS");

        HBox filaBotonesBusqueda = crearFilaBotones(
                btnEjecutarBfs,
                btnEjecutarDfs
        );

        this.recorridoLabel = crearCajaResultado("Recorrido: -");

        // --- Sección 3b: Ruta Óptima (Dijkstra) ---
        Label tituloDijkstra = crearTituloSeccion("Ruta Óptima (Dijkstra)");
        this.origenDijkstraCombo = crearComboEnteros("Origen");
        this.destinoDijkstraCombo = crearComboEnteros("Destino");
        this.btnCalcularDijkstra = new BotonSimple("Calcular ruta");
        this.btnCalcularDijkstra.setMaxWidth(Double.MAX_VALUE);
        this.resultadoDijkstraLabel = crearCajaResultado(
                "Origen: -\nDestino: -\nRuta: -\nCosto total: -"
        );

        // --- Sección 3c: Ordenamiento (QuickSort / Shell Sort) ---
        Label tituloOrden = crearTituloSeccion("Ordenamiento");
        Label etiquetaCriterio = crearSubetiqueta("Criterio");

        this.criterioOrdenCombo = new ComboBox<>();
        this.criterioOrdenCombo.setItems(FXCollections.observableArrayList(
                "Peso de la arista",
                "ID / Nombre de vértice"
        ));
        this.criterioOrdenCombo.setPromptText("Seleccionar criterio");
        this.criterioOrdenCombo.setMaxWidth(Double.MAX_VALUE);
        this.criterioOrdenCombo.getStyleClass().add("sidebar-combo");

        this.btnQuickSort = new BotonSimple("QuickSort");
        this.btnShellSort = new BotonSimple("Shell Sort");

        HBox filaBotonesOrden = crearFilaBotones(
                btnQuickSort,
                btnShellSort
        );

        this.resultadoOrdenLabel = crearCajaResultado("Ordenamiento: -");

        // --- Etiqueta de retroalimentación ---
        this.mensajeLabel = new Label();
        this.mensajeLabel.getStyleClass().add("sidebar-feedback");
        this.mensajeLabel.setWrapText(true);

        contenido.getChildren().addAll(
                tituloVertices,
                verticeField,
                nuevoVerticeField,
                filaBotonesVertice,
                new Separator(),
                tituloAristas,
                origenField,
                destinoField,
                pesoField,
                filaBotonesArista,
                new Separator(),
                tituloBusqueda,
                etiquetaInicial,
                verticeInicialCombo,
                filaBotonesBusqueda,
                recorridoLabel,
                new Separator(),
                tituloDijkstra,
                origenDijkstraCombo,
                destinoDijkstraCombo,
                btnCalcularDijkstra,
                resultadoDijkstraLabel,
                new Separator(),
                tituloOrden,
                etiquetaCriterio,
                criterioOrdenCombo,
                filaBotonesOrden,
                resultadoOrdenLabel,
                mensajeLabel
        );

        ScrollPane scrollPane = new ScrollPane(contenido);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("sidebar-scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        this.getChildren().add(scrollPane);
    }

    private Label crearTituloSeccion(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("sidebar-section-title");
        return label;
    }

    private Label crearSubetiqueta(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("sidebar-sublabel");
        return label;
    }

    private ComboBox<Integer> crearComboEnteros(String prompt) {
        ComboBox<Integer> combo = new ComboBox<>();
        combo.setPromptText(prompt);
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.getStyleClass().add("sidebar-combo");
        return combo;
    }

    private Label crearCajaResultado(String textoInicial) {
        Label label = new Label(textoInicial);
        label.getStyleClass().add("sidebar-recorrido");
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private TextField crearCampoTexto(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("sidebar-input");
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private HBox crearFilaBotones(BotonSimple... botones) {
        HBox fila = new HBox(6);
        fila.setAlignment(Pos.CENTER);
        for (BotonSimple boton : botones) {
            boton.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(boton, Priority.ALWAYS);
            fila.getChildren().add(boton);
        }
        return fila;
    }

    // ==========================================
    // Getters y helpers de Vértices / Aristas
    // ==========================================

    public String getVerticeInput() {
        return verticeField.getText().trim();
    }

    public String getNuevoVerticeInput() {
        return nuevoVerticeField.getText().trim();
    }

    public String getOrigenInput() {
        return origenField.getText().trim();
    }

    public String getDestinoInput() {
        return destinoField.getText().trim();
    }

    public String getPesoInput() {
        return pesoField.getText().trim();
    }

    public void limpiarCamposVertice() {
        verticeField.clear();
        nuevoVerticeField.clear();
    }

    public void limpiarCamposArista() {
        origenField.clear();
        destinoField.clear();
        pesoField.clear();
    }

    // ==========================================
    // Encapsulación de Búsqueda (BFS / DFS)
    // ==========================================

    public void setVerticesDisponibles(Collection<Integer> idsVertices) {
        sincronizarComboEnteros(verticeInicialCombo, idsVertices);
        sincronizarComboEnteros(origenDijkstraCombo, idsVertices);
        sincronizarComboEnteros(destinoDijkstraCombo, idsVertices);
    }

    private void sincronizarComboEnteros(ComboBox<Integer> combo, Collection<Integer> idsVertices) {
        Integer seleccionPrevia = combo.getValue();
        if (idsVertices == null || idsVertices.isEmpty()) {
            combo.setItems(FXCollections.observableArrayList());
            combo.setValue(null);
            return;
        }
        combo.setItems(FXCollections.observableArrayList(idsVertices));
        if (seleccionPrevia != null && idsVertices.contains(seleccionPrevia)) {
            combo.setValue(seleccionPrevia);
        } else {
            combo.setValue(null);
        }
    }

    public Integer getVerticeInicialSeleccionado() {
        return verticeInicialCombo.getValue();
    }

    public void setVerticeInicialSeleccionado(Integer idVertice) {
        verticeInicialCombo.setValue(idVertice);
    }

    public void mostrarRecorrido(String descripcionRecorrido) {
        if (descripcionRecorrido == null || descripcionRecorrido.isBlank()) {
            recorridoLabel.setText("Recorrido: -");
            return;
        }
        recorridoLabel.setText("Recorrido: " + descripcionRecorrido);
    }

    public void limpiarRecorrido() {
        recorridoLabel.setText("Recorrido: -");
    }

    // ==========================================
    // Encapsulación de Dijkstra
    // ==========================================

    public Integer getOrigenDijkstraSeleccionado() {
        return origenDijkstraCombo.getValue();
    }

    public Integer getDestinoDijkstraSeleccionado() {
        return destinoDijkstraCombo.getValue();
    }

    public void mostrarResultadoDijkstra(int origen, int destino, String ruta, double costoTotal) {
        String rutaTexto = (ruta == null || ruta.isBlank()) ? "-" : ruta;
        resultadoDijkstraLabel.setText(
                "Origen: " + origen + "\n"
                        + "Destino: " + destino + "\n"
                        + "Ruta: " + rutaTexto + "\n"
                        + "Costo total: " + costoTotal
        );
    }

    public void mostrarResultadoDijkstra(String texto) {
        resultadoDijkstraLabel.setText(
                (texto == null || texto.isBlank())
                        ? "Origen: -\nDestino: -\nRuta: -\nCosto total: -"
                        : texto
        );
    }

    // ==========================================
    // Encapsulación de Ordenamiento
    // ==========================================

    public String getCriterioOrdenSeleccionado() {
        return criterioOrdenCombo.getValue();
    }

    public void mostrarResultadoOrdenamiento(String resumen) {
        resultadoOrdenLabel.setText(
                (resumen == null || resumen.isBlank()) ? "Ordenamiento: -" : resumen
        );
    }

    public void mostrarMensaje(String texto) {
        this.mensajeLabel.setText(texto);
    }

    // ==========================================
    // Registro de eventos desde el Controlador
    // ==========================================

    public void setOnAgregarVertice(EventHandler<ActionEvent> handler) {
        this.btnAgregarVertice.setAction(handler);
    }

    public void setOnModificarVertice(EventHandler<ActionEvent> handler) {
        this.btnModificarVertice.setAction(handler);
    }

    public void setOnEliminarVertice(EventHandler<ActionEvent> handler) {
        this.btnEliminarVertice.setAction(handler);
    }

    public void setOnAgregarArista(EventHandler<ActionEvent> handler) {
        this.btnAgregarArista.setAction(handler);
    }

    public void setOnModificarArista(EventHandler<ActionEvent> handler) {
        this.btnModificarArista.setAction(handler);
    }

    public void setOnEliminarArista(EventHandler<ActionEvent> handler) {
        this.btnEliminarArista.setAction(handler);
    }

    public void setOnEjecutarBfs(EventHandler<ActionEvent> handler) {
        this.btnEjecutarBfs.setAction(handler);
    }

    public void setOnEjecutarDfs(EventHandler<ActionEvent> handler) {
        this.btnEjecutarDfs.setAction(handler);
    }

    public void setOnCalcularDijkstra(EventHandler<ActionEvent> handler) {
        this.btnCalcularDijkstra.setAction(handler);
    }

    public void setOnQuickSort(EventHandler<ActionEvent> handler) {
        this.btnQuickSort.setAction(handler);
    }

    public void setOnShellSort(EventHandler<ActionEvent> handler) {
        this.btnShellSort.setAction(handler);
    }
}