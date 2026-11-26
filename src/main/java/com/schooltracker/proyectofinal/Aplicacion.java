package com.schooltracker.proyectofinal;

import com.schooltracker.proyectofinal.View.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacion extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainView root = new MainView();
        Scene scene = new Scene(root, 720, 480);
        stage.setTitle("Proyecto Final");
        stage.setScene(scene);
        stage.show();
    }
}
