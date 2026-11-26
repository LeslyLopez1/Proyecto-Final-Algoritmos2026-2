package com.schooltracker.proyectofinal;

import com.schooltracker.proyectofinal.Model.Archivos.Lector;
import com.schooltracker.proyectofinal.Model.Arista;
import com.schooltracker.proyectofinal.Model.Grafo;
import com.schooltracker.proyectofinal.Model.MainModel;
import com.schooltracker.proyectofinal.Model.Vertice;
import com.schooltracker.proyectofinal.View.GrafoView;
import com.schooltracker.proyectofinal.View.MainView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controlador {

    private final MainModel model;
    private final MainView view;

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

        view.getNavBar().onCargarCsv(event -> cargarGrafoDesdeSelector());
    }

    public void cargarGrafoDesdeSelector() {
        String rutaSeleccionada = view.getNavBar().getRutaCsvSeleccionado();
        boolean dirigido = view.getNavBar().isGrafoDirigido();

        try {
            if (rutaSeleccionada != null && !rutaSeleccionada.isBlank()) {
                model.cargarDesdeRecurso(rutaSeleccionada, dirigido);
            } else {
                model.cargarDesdeRecurso(Lector.RUTA_GRAFO_PEQUE, dirigido);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el CSV seleccionado: " + rutaSeleccionada, e);
        }

        refrescarVistaGrafo();
    }

    public void refrescarVistaGrafo() {
        Grafo grafo = model.getGrafoActual();
        if (grafo == null) {
            view.actualizarGrafoView(new GrafoView(view.getNavBar().isGrafoDirigido()));
            return;
        }

        boolean dirigido = view.getNavBar().isGrafoDirigido();
        List<String> etiquetasVertices = new ArrayList<>();
        for (Vertice v : grafo.getVertices()) {
            etiquetasVertices.add(v.getEtiqueta());
        }

        List<String> conexiones = new ArrayList<>();
        for (Arista a : grafo.getTodasLasAristas()) {
            conexiones.add(
                    a.getOrigen().getEtiqueta()
                            + " -> "
                            + a.getDestino().getEtiqueta()
                            + " ("
                            + a.getPeso()
                            + ")"
            );
        }

        GrafoView nuevaVista = new GrafoView(dirigido);
        nuevaVista.establecerDatos(etiquetasVertices, conexiones);
        view.actualizarGrafoView(nuevaVista);
    }

    public MainModel getModel() {
        return model;
    }

    public MainView getView() {
        return view;
    }
}