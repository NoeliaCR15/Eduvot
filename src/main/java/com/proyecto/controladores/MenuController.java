package com.proyecto.controladores;

import java.io.IOException;

import com.proyecto.modelos.Usuario;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblRolUsuario;

    public void inicializarUsuario(Usuario usuario) {
        lblNombreUsuario.setText("EduVot Admin - " + usuario.getNombreUsuario());
        lblRolUsuario.setText("Administrador");
    }

    @FXML
    private void abrirUsuarios() {
        abrirVentana("/com/Interfaz/GestionUsuarios.fxml", "Gestion de usuarios");
    }

    @FXML
    private void abrirSubcategorias() {
        mostrarModuloPendiente("Subcategorias", "Aqui construiremos la gestion de subcategorias y colectivos.");
    }

    @FXML
    private void abrirEncuestas() {
        mostrarModuloPendiente("Encuestas", "Aqui desarrollaremos la creacion y administracion de encuestas.");
    }

    @FXML
    private void abrirResultados() {
        mostrarModuloPendiente("Resultados", "Aqui mostraremos recuentos, participacion y resultados.");
    }

    @FXML
    private void salirApp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/login.fxml"));
            Parent root = loader.load();
            Stage stageActual = (Stage) lblNombreUsuario.getScene().getWindow();
            stageActual.setTitle("EduVot - Inicio de sesion");
            stageActual.setScene(new Scene(root, 600, 400));
            stageActual.show();
        } catch (IOException e) {
            System.out.println("Error al cerrar sesion: " + e.getMessage());
        }
    }

    private void mostrarModuloPendiente(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("Modulo en preparacion");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVentana(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al abrir ventana: " + e.getMessage());
        }
    }
}
