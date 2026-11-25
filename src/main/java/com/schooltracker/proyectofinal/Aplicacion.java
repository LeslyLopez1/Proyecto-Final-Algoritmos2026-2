package com.schooltracker.proyectofinal;

import com.schooltracker.proyectofinal.View.MainScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Aplicacion extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainScreen root = new MainScreen();
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Proyecto Final");
        stage.setScene(scene);
        stage.show();
    }
}
