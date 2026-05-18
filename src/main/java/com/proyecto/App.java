package com.proyecto;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    App.class.getResource("/com/Interfaz/login.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 430, 680);
            stage.setTitle("EduVot - Inicio de sesion");
            stage.setMinWidth(390);
            stage.setMinHeight(620);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Error inesperado al iniciar JavaFX: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            launch();
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicacion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
