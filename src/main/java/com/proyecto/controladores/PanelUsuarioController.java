package com.proyecto.controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.modelos.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PanelUsuarioController {

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblGrupoUsuario;

    @FXML
    private VBox contenidoUsuario;

    private List<Node> contenidoDashboard;

    @FXML
    public void initialize() {
        contenidoDashboard = new ArrayList<>(contenidoUsuario.getChildren());
    }

    public void inicializarUsuario(Usuario usuario) {
        lblNombreUsuario.setText(usuario.getNombreUsuario());
        lblGrupoUsuario.setText("Curso: " + usuario.getGrupo());
    }

    @FXML
    private void abrirVotaciones() {
        mostrarVistaPendiente(
                "Votaciones disponibles",
                "Aqui apareceran las encuestas disponibles para tu curso y subcategoria.");
    }

    @FXML
    private void abrirMisVotos() {
        mostrarVistaPendiente(
                "Mi participacion",
                "Aqui podras consultar las votaciones en las que ya has participado.");
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

    @FXML
    private void volverDashboard() {
        contenidoUsuario.getChildren().setAll(contenidoDashboard);
    }

    private void mostrarVistaPendiente(String titulo, String mensaje) {
        HBox cabecera = new HBox(18);
        cabecera.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        cabecera.getStyleClass().add("user-hero");
        cabecera.setPadding(new Insets(26, 28, 26, 28));

        VBox textos = new VBox(8);
        Label etiqueta = new Label("Usuario");
        etiqueta.getStyleClass().add("hero-badge");
        Label tituloVista = new Label(titulo);
        tituloVista.getStyleClass().add("hero-title");
        tituloVista.setWrapText(true);
        Label descripcion = new Label(mensaje);
        descripcion.getStyleClass().add("hero-text");
        descripcion.setWrapText(true);
        textos.getChildren().addAll(etiqueta, tituloVista, descripcion);

        Region separador = new Region();
        HBox.setHgrow(separador, javafx.scene.layout.Priority.ALWAYS);

        Button volver = new Button("Volver");
        volver.getStyleClass().add("secondary-button");
        volver.setOnAction(event -> volverDashboard());

        cabecera.getChildren().addAll(textos, separador, volver);

        VBox tarjeta = new VBox(14);
        tarjeta.getStyleClass().add("module-card");
        tarjeta.setPadding(new Insets(24));
        Label tituloTarjeta = new Label("Modulo en preparacion");
        tituloTarjeta.getStyleClass().add("module-title");
        Label textoTarjeta = new Label(mensaje);
        textoTarjeta.getStyleClass().add("module-text");
        textoTarjeta.setWrapText(true);
        tarjeta.getChildren().addAll(tituloTarjeta, textoTarjeta);

        contenidoUsuario.getChildren().setAll(cabecera, tarjeta);
    }
}
