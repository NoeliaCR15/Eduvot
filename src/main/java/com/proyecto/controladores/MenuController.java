package com.proyecto.controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.modelos.Usuario;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblRolUsuario;

    @FXML
    private VBox contenidoPrincipal;

    private List<Node> contenidoDashboard;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        // Guarda el dashboard inicial para poder volver desde las vistas internas.
        contenidoDashboard = new ArrayList<>(contenidoPrincipal.getChildren());
    }

    public void inicializarUsuario(Usuario usuario) {
        usuarioActual = usuario;
        lblNombreUsuario.setText("EduVot Admin - " + usuario.getNombreUsuario());
        lblRolUsuario.setText("Administrador");
    }

    @FXML
    private void abrirUsuarios() {
        cargarVistaUsuarios();
    }

    @FXML
    private void abrirSubcategorias() {
        cargarVistaSubcategorias();
    }

    @FXML
    private void abrirEncuestas() {
        cargarVistaEncuestas();
    }

    @FXML
    private void abrirResultados() {
        cargarVistaResultados();
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

    public void mostrarDashboard() {
        contenidoPrincipal.getChildren().setAll(contenidoDashboard);
    }

    private void cargarVistaUsuarios() {
        try {
            // Carga la vista dentro del panel principal sin abrir una ventana nueva.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/GestionUsuarios.fxml"));
            Parent root = loader.load();
            GestionUsuariosController controller = loader.getController();
            controller.setMenuController(this);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            contenidoPrincipal.getChildren().setAll(scrollPane);
        } catch (IOException e) {
            System.out.println("Error al cargar gestion de usuarios: " + e.getMessage());
        }
    }

    private void cargarVistaSubcategorias() {
        try {
            // Mismo patron de carga que usuarios: FXML + controlador + ScrollPane.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/GestionSubcategorias.fxml"));
            Parent root = loader.load();
            GestionSubcategoriasController controller = loader.getController();
            controller.setMenuController(this);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            contenidoPrincipal.getChildren().setAll(scrollPane);
        } catch (IOException e) {
            System.out.println("Error al cargar gestion de subcategorias: " + e.getMessage());
        }
    }

    private void cargarVistaEncuestas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/GestionEncuestas.fxml"));
            Parent root = loader.load();
            GestionEncuestasController controller = loader.getController();
            controller.setMenuController(this);
            controller.setUsuarioActual(usuarioActual);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            contenidoPrincipal.getChildren().setAll(scrollPane);
        } catch (IOException e) {
            System.out.println("Error al cargar gestion de encuestas: " + e.getMessage());
        }
    }

    private void cargarVistaResultados() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/Resultados.fxml"));
            Parent root = loader.load();
            ResultadosController controller = loader.getController();
            controller.setMenuController(this);
            controller.setUsuarioActual(usuarioActual);

            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            contenidoPrincipal.getChildren().setAll(scrollPane);
        } catch (IOException e) {
            System.out.println("Error al cargar resultados: " + e.getMessage());
        }
    }
}
