package com.schooltracker.proyectofinal;

import com.schooltracker.proyectofinal.Algoritmos.BFS;
import com.schooltracker.proyectofinal.Algoritmos.CriteriosOrden;
import com.schooltracker.proyectofinal.Algoritmos.DFS;
import com.schooltracker.proyectofinal.Algoritmos.Dijkstra;
import com.schooltracker.proyectofinal.Algoritmos.QuickSort;
import com.schooltracker.proyectofinal.Algoritmos.Resultado;
import com.schooltracker.proyectofinal.Algoritmos.ShellSort;
import com.schooltracker.proyectofinal.Model.Archivos.Escritor;
import com.schooltracker.proyectofinal.Model.Archivos.Lector;
import com.schooltracker.proyectofinal.Model.Archivos.ResultadoImportacion;
import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.MainModel;
import com.schooltracker.proyectofinal.Model.Vertice;
import com.schooltracker.proyectofinal.View.GrafoView;
import com.schooltracker.proyectofinal.View.MainView;
import com.schooltracker.proyectofinal.View.VentanaOrden;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class Controlador {

    private static final int MAXIMO_VERTICES_DIBUJADOS = 50;
    private static final int MAXIMO_VERTICES_MOSTRADOS = 40;

    private final MainModel model;
    private final MainView view;
    private final Lector lector = new Lector();
    private final Escritor escritor = new Escritor();

    public Controlador(MainModel model, MainView view) {
        if (model == null) {
            throw new IllegalArgumentException("MainModel no puede ser nulo.");
        }
        if (view == null) {
            throw new IllegalArgumentException("MainView no puede ser nula.");
        }
        this.model = model;
        this.view = view;
        configurarEventos();
    }

    private void configurarEventos() {
        view.getNavBar().onTipoGrafoChange((obs, oldVal, esDirigido) -> {
            model.setDirigido(esDirigido);
            refrescarVistaGrafo();
        });

        // Botón "Grafo" / "Analisis": cambia lo que se muestra en el centro
        view.getNavBar().onVistaChange((obs, oldVal, esVistaGrafo) -> {
            if (esVistaGrafo) {
                view.mostrarGrafo();
            } else {
                view.mostrarAnalisis();
            }
        });

        // Archivos CSV: combo con los CSV del proyecto y botones para abrir/guardar cualquier archivo
        view.getNavBar().onSeleccionCsv(e -> cargarCsvDelCombo());
        Button botonAbrir = new Button("Abrir CSV...");
        botonAbrir.setOnAction(e -> abrirCsv());
        Button botonGuardar = new Button("Guardar CSV");
        botonGuardar.setOnAction(e -> guardarCsv());
        view.getNavBar().addAction(botonAbrir);
        view.getNavBar().addAction(botonGuardar);

        // Punto 1: vértices y aristas
        view.getSideBar().setOnAgregarVertice(e -> agregarVertice());
        view.getSideBar().setOnModificarVertice(e -> modificarVertice());
        view.getSideBar().setOnEliminarVertice(e -> eliminarVertice());
        view.getSideBar().setOnAgregarArista(e -> agregarArista());
        view.getSideBar().setOnModificarArista(e -> modificarArista());
        view.getSideBar().setOnEliminarArista(e -> eliminarArista());

        // Punto 3: BFS y DFS
        view.getSideBar().setOnEjecutarBfs(e -> recorrerGrafo(true));
        view.getSideBar().setOnEjecutarDfs(e -> recorrerGrafo(false));

        // Punto 4: ruta óptima con Dijkstra
        view.getSideBar().setOnCalcularDijkstra(e -> calcularDijkstra());

        // Punto 5: ordenamiento de las conexiones
        view.getSideBar().setOnQuickSort(e -> ordenarConexiones(true));
        view.getSideBar().setOnShellSort(e -> ordenarConexiones(false));
    }

    // ==========================================================
    // Dibujo del grafo
    // ==========================================================

    public void refrescarVistaGrafo() {
        Grafo grafo = model.getGrafoActual();
        actualizarCombosVertices(grafo);
        boolean dirigido = view.getNavBar().isGrafoDirigido();
        GrafoView nuevaVista = new GrafoView(dirigido);

        if (grafo == null) {
            view.actualizarGrafoView(nuevaVista);
            return;
        }

        List<Arista> aristas = grafo.getTodasLasAristas();
        int totalVertices = grafo.getVertices().size();

        // Si el grafo es muy grande se dibujan solo los primeros 50 vértices y las conexiones entre ellos
        boolean recortado = totalVertices > MAXIMO_VERTICES_DIBUJADOS;
        List<String> etiquetasVertices = new ArrayList<>();
        Set<Integer> idsDibujados = new HashSet<>();
        for (Vertice v : grafo.getVertices()) {
            if (etiquetasVertices.size() < MAXIMO_VERTICES_DIBUJADOS) {
                etiquetasVertices.add(v.getEtiqueta());
                idsDibujados.add(v.getId());
            }
        }

        List<String> conexiones = new ArrayList<>();
        for (Arista a : aristas) {
            if (recortado && !(idsDibujados.contains(a.getOrigen().getId()) && idsDibujados.contains(a.getDestino().getId()))) {
                continue;
            }
            conexiones.add(
                    a.getOrigen().getEtiqueta()
                            + " -> "
                            + a.getDestino().getEtiqueta()
                            + " ("
                            + a.getPeso()
                            + ")"
            );
        }

        nuevaVista.establecerDatos(etiquetasVertices, conexiones);
        if (recortado) {
            nuevaVista.setNota("Mostrando " + etiquetasVertices.size() + " de " + totalVertices + " vértices y "
                    + conexiones.size() + " de " + aristas.size() + " conexiones. Los algoritmos usan el grafo completo.");
        }
        view.actualizarGrafoView(nuevaVista);
        model.getGrafoActual().imprimirMatrizAdyacencia();
    }

    /** Llena los combos de vértices (BFS/DFS y Dijkstra) con los nombres del grafo actual. */
    private void actualizarCombosVertices(Grafo grafo) {
        Map<Integer, String> etiquetas = new LinkedHashMap<>();
        if (grafo != null) {
            for (Vertice v : grafo.getVertices()) {
                etiquetas.put(v.getId(), v.getEtiqueta());
            }
        }
        view.getSideBar().setVerticesDisponibles(etiquetas);
    }

    /** Después de cambiar el grafo se borran los resultados anteriores (ya no corresponden) y se redibuja. */
    private void grafoCambio() {
        view.getSideBar().limpiarRecorrido();
        view.getSideBar().mostrarResultadoDijkstra("");
        view.getSideBar().mostrarResultadoOrdenamiento("");
        refrescarVistaGrafo();
    }

    private void mensaje(String texto) {
        view.getSideBar().mostrarMensaje(texto);
    }

    // ==========================================================
    // Punto 1: vértices
    // ==========================================================

    private void agregarVertice() {
        String texto = view.getSideBar().getVerticeInput();
        if (texto.isEmpty()) {
            mensaje("Escribe el id (entero) o el nombre del vértice.");
            return;
        }

        int id;
        String etiqueta;
        try {
            id = Integer.parseInt(texto);                    // "5" -> vértice con id 5
            String nombre = view.getSideBar().getNuevoVerticeInput();
            etiqueta = nombre.isEmpty() ? String.valueOf(id) : nombre;
        } catch (NumberFormatException e) {
            id = siguienteId();                              // "Biblioteca" -> el programa le da un id libre
            etiqueta = texto;
        }

        if (id < 1) {
            mensaje("El id debe ser un entero positivo.");
        } else if (etiqueta.contains(",")) {
            mensaje("El nombre no puede llevar comas.");
        } else if (model.getGrafoActual().getVertice(id) != null) {
            mensaje("Ya existe un vértice con el id " + id + ".");
        } else if (nombreRepetido(etiqueta, -1)) {
            mensaje("Ya existe un vértice llamado '" + etiqueta + "'.");
        } else {
            model.agregarVertice(id, etiqueta);
            view.getSideBar().limpiarCamposVertice();
            grafoCambio();
            mensaje("Vértice '" + etiqueta + "' agregado.");
        }
    }

    private void modificarVertice() {
        Integer id = buscarVertice(view.getSideBar().getVerticeInput());
        String nuevoNombre = view.getSideBar().getNuevoVerticeInput();

        if (id == null) {
            mensaje("No existe ese vértice. Escribe su id o su nombre.");
        } else if (nuevoNombre.isEmpty()) {
            mensaje("Escribe el nuevo nombre en el segundo campo.");
        } else if (nuevoNombre.contains(",")) {
            mensaje("El nombre no puede llevar comas.");
        } else if (nombreRepetido(nuevoNombre, id)) {
            mensaje("Ya existe un vértice llamado '" + nuevoNombre + "'.");
        } else {
            model.modificarEtiquetaVertice(id, nuevoNombre);
            view.getSideBar().limpiarCamposVertice();
            grafoCambio();
            mensaje("Vértice renombrado a '" + nuevoNombre + "'.");
        }
    }

    private void eliminarVertice() {
        Integer id = buscarVertice(view.getSideBar().getVerticeInput());
        if (id == null) {
            mensaje("No existe ese vértice. Escribe su id o su nombre.");
            return;
        }
        String nombre = model.getGrafoActual().getVertice(id).getEtiqueta();
        model.eliminarVertice(id);   // también se borran sus conexiones
        view.getSideBar().limpiarCamposVertice();
        grafoCambio();
        mensaje("Vértice '" + nombre + "' eliminado junto con sus conexiones.");
    }

    // ==========================================================
    // Punto 1: aristas
    // ==========================================================

    private void agregarArista() {
        Integer origen = buscarVertice(view.getSideBar().getOrigenInput());
        Integer destino = buscarVertice(view.getSideBar().getDestinoInput());
        Double peso = leerPeso();
        if (!extremosValidos(origen, destino) || peso == null) {
            return;
        }

        if (model.agregarArista(origen, destino, peso)) {
            view.getSideBar().limpiarCamposArista();
            grafoCambio();
            mensaje("Conexión " + nombreDe(origen) + " -> " + nombreDe(destino) + " agregada.");
        } else {
            mensaje("Esa conexión ya existe. Usa Modificar para cambiar su peso.");
        }
    }

    private void modificarArista() {
        Integer origen = buscarVertice(view.getSideBar().getOrigenInput());
        Integer destino = buscarVertice(view.getSideBar().getDestinoInput());
        Double peso = leerPeso();
        if (!extremosValidos(origen, destino) || peso == null) {
            return;
        }

        if (model.modificarPesoArista(origen, destino, peso)) {
            view.getSideBar().limpiarCamposArista();
            grafoCambio();
            mensaje("Peso de " + nombreDe(origen) + " -> " + nombreDe(destino) + " cambiado a " + peso + ".");
        } else {
            mensaje("No existe esa conexión.");
        }
    }

    private void eliminarArista() {
        Integer origen = buscarVertice(view.getSideBar().getOrigenInput());
        Integer destino = buscarVertice(view.getSideBar().getDestinoInput());
        if (!extremosValidos(origen, destino)) {
            return;
        }

        if (model.eliminarArista(origen, destino)) {
            view.getSideBar().limpiarCamposArista();
            grafoCambio();
            mensaje("Conexión " + nombreDe(origen) + " -> " + nombreDe(destino) + " eliminada.");
        } else {
            mensaje("No existe esa conexión.");
        }
    }

    /** Revisa que origen y destino existan y sean distintos (no se permiten bucles). */
    private boolean extremosValidos(Integer origen, Integer destino) {
        if (origen == null || destino == null) {
            mensaje("El origen y el destino deben ser vértices que ya existen (id o nombre).");
            return false;
        }
        if (origen.equals(destino)) {
            mensaje("No se permiten conexiones de un vértice consigo mismo.");
            return false;
        }
        return true;
    }

    private Double leerPeso() {
        String texto = view.getSideBar().getPesoInput();
        try {
            double peso = Double.parseDouble(texto);
            if (peso < 0 || Double.isNaN(peso) || Double.isInfinite(peso)) {
                mensaje("El peso debe ser un número mayor o igual que 0.");
                return null;
            }
            return peso;
        } catch (NumberFormatException e) {
            mensaje("El peso debe ser un número (por ejemplo 120 o 3.5).");
            return null;
        }
    }

    // ==========================================================
    // Ayudas para vértices
    // ==========================================================

    /** Busca un vértice por su nombre y, si no hay, por su id. Regresa null si no existe. */
    private Integer buscarVertice(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        Grafo grafo = model.getGrafoActual();
        for (Vertice v : grafo.getVertices()) {
            if (v.getEtiqueta().equalsIgnoreCase(texto.trim())) {
                return v.getId();
            }
        }
        try {
            int id = Integer.parseInt(texto.trim());
            if (grafo.getVertice(id) != null) {
                return id;
            }
        } catch (NumberFormatException e) {
            // no era un número: no existe
        }
        return null;
    }

    private String nombreDe(int id) {
        return model.getGrafoActual().getVertice(id).getEtiqueta();
    }

    private int siguienteId() {
        int mayor = 0;
        for (Vertice v : model.getGrafoActual().getVertices()) {
            mayor = Math.max(mayor, v.getId());
        }
        return mayor + 1;
    }

    /** ¿Otro vértice (distinto de idPropio) ya tiene ese nombre? */
    private boolean nombreRepetido(String nombre, int idPropio) {
        for (Vertice v : model.getGrafoActual().getVertices()) {
            if (v.getId() != idPropio && v.getEtiqueta().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // ==========================================================
    // Punto 3: BFS y DFS
    // ==========================================================

    private void recorrerGrafo(boolean anchura) {
        Integer inicioId = view.getSideBar().getVerticeInicialSeleccionado();
        if (inicioId == null) {
            mensaje("Selecciona el vértice inicial.");
            return;
        }

        List<Vertice> orden = anchura
                ? BFS.recorrer(model.getGrafoActual(), inicioId)
                : DFS.recorrer(model.getGrafoActual(), inicioId);

        StringBuilder texto = new StringBuilder();
        int mostrados = Math.min(orden.size(), MAXIMO_VERTICES_MOSTRADOS);
        for (int i = 0; i < mostrados; i++) {
            if (i > 0) {
                texto.append(" -> ");
            }
            texto.append(orden.get(i).getEtiqueta());
        }
        if (orden.size() > mostrados) {
            texto.append(" ... (" + orden.size() + " vértices en total)");
        }

        view.getSideBar().mostrarRecorrido((anchura ? "BFS" : "DFS") + "\n" + texto);
        mensaje("");
    }

    // ==========================================================
    // Punto 4: Dijkstra
    // ==========================================================

    /** Lee origen y destino del panel lateral y muestra el resultado de Dijkstra. */
    private void calcularDijkstra() {
        Integer origenId = view.getSideBar().getOrigenDijkstraSeleccionado();
        Integer destinoId = view.getSideBar().getDestinoDijkstraSeleccionado();
        if (origenId == null || destinoId == null) {
            mensaje("Selecciona el origen y el destino.");
            return;
        }

        try {
            Resultado resultado = Dijkstra.calcular(model.getGrafoActual(), origenId, destinoId);
            String origen = resultado.getOrigen().getEtiqueta();
            String destino = resultado.getDestino().getEtiqueta();

            if (!resultado.isEncontrada()) {
                view.getSideBar().mostrarResultadoDijkstra("Origen: " + origen + "\nDestino: " + destino
                        + "\nNo existe una ruta entre ambos vértices.");
            } else {
                StringBuilder ruta = new StringBuilder();
                for (int i = 0; i < resultado.getCamino().size(); i++) {
                    if (i > 0) {
                        ruta.append(" -> ");
                    }
                    ruta.append(resultado.getCamino().get(i).getEtiqueta());
                }
                view.getSideBar().mostrarResultadoDijkstra("Origen: " + origen + "\nDestino: " + destino
                        + "\nRuta: " + ruta + "\nCosto total: " + resultado.getCosto());
            }
            mensaje("");
        } catch (IllegalArgumentException ex) {
            mensaje(ex.getMessage());
        }
    }

    // ==========================================================
    // Punto 5: ordenamiento
    // ==========================================================

    /** Ordena las conexiones con QuickSort o Shell Sort según el criterio elegido. */
    private void ordenarConexiones(boolean usarQuickSort) {
        String criterio = view.getSideBar().getCriterioOrdenSeleccionado();
        if (criterio == null) {
            mensaje("Selecciona el criterio de ordenamiento.");
            return;
        }

        boolean porPeso = criterio.startsWith("Peso");
        Comparator<Arista> comparador = porPeso ? CriteriosOrden.POR_PESO : CriteriosOrden.POR_NOMBRE;
        String algoritmo = usarQuickSort ? "QuickSort" : "Shell Sort";

        List<Arista> lista = new ArrayList<>(model.getGrafoActual().getTodasLasAristas());
        if (lista.isEmpty()) {
            mensaje("El grafo no tiene conexiones para ordenar.");
            return;
        }

        long inicio = System.nanoTime();
        try {
            if (usarQuickSort) {
                QuickSort.ordenar(lista, comparador);
            } else {
                ShellSort.ordenar(lista, comparador);
            }
        } catch (StackOverflowError ex) {
            mensaje("QuickSort se desbordó con esta lista; prueba con Shell Sort.");
            return;
        }
        double milisegundos = (System.nanoTime() - inicio) / 1000000.0;

        List<String> lineas = new ArrayList<>();
        for (Arista a : lista) {
            lineas.add(a.getOrigen().getEtiqueta() + " -> " + a.getDestino().getEtiqueta() + " (" + a.getPeso() + ")");
        }

        String criterioTexto = porPeso ? "peso" : "nombre";
        view.getSideBar().mostrarResultadoOrdenamiento(algoritmo + " por " + criterioTexto + ":\n"
                + lista.size() + " conexiones en " + String.format("%.3f", milisegundos) + " ms");
        mensaje("");
        VentanaOrden.mostrar(algoritmo + " - conexiones ordenadas por " + criterioTexto, lineas);
    }

    // ==========================================================
    // Puntos 1 y 2: abrir y guardar archivos CSV
    // ==========================================================

    private void cargarCsvDelCombo() {
        String ruta = view.getNavBar().getRutaCsvSeleccionado();
        if (ruta == null) {
            return;
        }
        try {
            ResultadoImportacion resultado = lector.importarDesdeRecurso(ruta, view.getNavBar().isGrafoDirigido());
            aplicarImportacion(resultado, view.getNavBar().getArchivoCsvSeleccionado());
        } catch (IOException ex) {
            mensaje("No se pudo leer el archivo: " + ex.getMessage());
        }
    }

    private void abrirCsv() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Abrir archivo CSV");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = selector.showOpenDialog(view.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        try {
            ResultadoImportacion resultado = lector.importarDesdeArchivo(archivo, view.getNavBar().isGrafoDirigido());
            aplicarImportacion(resultado, archivo.getName());
        } catch (IOException ex) {
            mensaje("No se pudo leer el archivo: " + ex.getMessage());
        }
    }

    /** Si el archivo no tiene errores reemplaza el grafo; si los tiene, los muestra y deja el grafo como estaba. */
    private void aplicarImportacion(ResultadoImportacion resultado, String nombreArchivo) {
        if (resultado.tieneErrores()) {
            TextArea errores = new TextArea(String.join("\n", resultado.getErrores()));
            errores.setEditable(false);
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Errores en el archivo CSV");
            alerta.setHeaderText(nombreArchivo + ": no se importó el grafo");
            alerta.getDialogPane().setContent(errores);
            alerta.show();
            mensaje("El archivo " + nombreArchivo + " tiene errores; el grafo no cambió.");
            return;
        }

        model.establecerGrafo(resultado.getGrafo());
        grafoCambio();

        String resumen = "Vértices importados: " + resultado.getVertices()
                + "\nAristas importadas: " + resultado.getAristas();
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Importación correcta");
        alerta.setHeaderText(nombreArchivo);
        alerta.setContentText(resumen);
        alerta.show();
        mensaje(nombreArchivo + ": " + resultado.getVertices() + " vértices y " + resultado.getAristas() + " aristas.");
    }

    private void guardarCsv() {
        Grafo grafo = model.getGrafoActual();
        if (grafo.getTodasLasAristas().isEmpty()) {
            mensaje("No hay conexiones que guardar.");
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar grafo como CSV");
        selector.setInitialFileName("grafo.csv");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        File archivo = selector.showSaveDialog(view.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".csv");
        }

        try {
            int guardadas = escritor.guardar(grafo, archivo);
            int aislados = 0;
            for (Vertice v : grafo.getVertices()) {
                if (!grafo.tieneConexiones(v.getId())) {
                    aislados++;
                }
            }
            String texto = "Guardado " + archivo.getName() + ": " + guardadas + " conexiones.";
            if (aislados > 0) {
                texto += " (" + aislados + " vértice(s) sin conexiones no se guardan.)";
            }
            mensaje(texto);
        } catch (IOException | IllegalArgumentException ex) {
            mensaje("No se pudo guardar: " + ex.getMessage());
        }
    }

    public MainModel getModel() {
        return model;
    }

    public MainView getView() {
        return view;
    }
}
