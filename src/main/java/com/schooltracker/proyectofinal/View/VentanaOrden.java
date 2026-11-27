package com.schooltracker.proyectofinal.View;

import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/** Ventana con la lista de conexiones ya ordenadas (puede ser larga, por eso lleva scroll). */
public class VentanaOrden {

    public static void mostrar(String titulo, List<String> conexiones) {
        TextArea area = new TextArea();
        area.setEditable(false);
        area.setStyle("-fx-font-family: monospace;");

        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < conexiones.size(); i++) {
            texto.append(i + 1).append(". ").append(conexiones.get(i)).append("\n");
        }
        area.setText(texto.toString());

        Stage ventana = new Stage();
        ventana.setTitle(titulo);
        ventana.setScene(new Scene(area, 460, 520));
        ventana.show();
    }
}
