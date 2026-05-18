module com.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.proyecto to javafx.fxml;
    opens com.proyecto.controladores to javafx.fxml;

    exports com.proyecto;
    exports com.proyecto.controladores;
}
