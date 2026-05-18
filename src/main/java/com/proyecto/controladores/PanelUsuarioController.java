package com.proyecto.controladores;

import java.io.IOException;

import com.proyecto.modelos.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PanelUsuarioController {

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblGrupoUsuario;

    public void inicializarUsuario(Usuario usuario) {
        lblNombreUsuario.setText(usuario.getNombreUsuario());
        lblGrupoUsuario.setText("Curso: " + usuario.getGrupo());
    }

    @FXML
    private void abrirVotaciones() {
        mostrarModuloPendiente("Votaciones", "Aqui apareceran las encuestas disponibles para tu curso y subcategoria.");
    }

    @FXML
    private void abrirMisVotos() {
        mostrarModuloPendiente("Mis votos", "Aqui podras consultar las votaciones en las que ya has participado.");
    }

    @FXML
    private void cerrarSesion() {
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
}
