module com.schooltracker.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.schooltracker.proyectofinal to javafx.fxml;
    exports com.schooltracker.proyectofinal;
}