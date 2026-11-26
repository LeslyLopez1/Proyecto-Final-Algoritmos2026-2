package com.schooltracker.proyectofinal;

import com.schooltracker.proyectofinal.Model.MainModel;
import com.schooltracker.proyectofinal.View.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacion extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainModel model = new MainModel();
        MainView view = new MainView();
        Controlador controlador = new Controlador(model, view);

        model.cargarGrafoPeque();
        controlador.refrescarVistaGrafo();

        Scene scene = new Scene(view, 720, 480);
        stage.setTitle("Proyecto Final");
        stage.setScene(scene);
        stage.show();
    }
}
