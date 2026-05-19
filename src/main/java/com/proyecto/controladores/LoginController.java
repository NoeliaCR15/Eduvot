package com.proyecto.controladores;

import java.io.IOException;

import com.proyecto.dao.UsuarioDAO;
import com.proyecto.modelos.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private Button btnVerPassword;

    @FXML
    private StackPane passwordContainer;

    @FXML
    private SVGPath iconoPassword;

    @FXML
    private Label lblMensaje;

    private UsuarioDAO usuarioDAO;
    private boolean passwordVisible;

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        // Ambos campos comparten el mismo texto para alternar entre oculto y visible.
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
        passwordContainer.setOnMouseClicked(event -> {
            if (!esClickEnBotonPassword(event.getTarget())) {
                enfocarCampoPassword();
            }
        });
    }

    @FXML
    private void iniciarSesion() {
        String dni = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (dni.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Debes rellenar DNI y contrasena.");
            return;
        }

        Usuario usuario = usuarioDAO.validarLogin(dni, password);

        if (usuario == null) {
            lblMensaje.setText("DNI o contrasena incorrectos.");
            return;
        }

        // El rol del usuario decide que panel se abre tras iniciar sesion.
        if (usuario.isEsAdministrador()) {
            abrirPanelAdministrador(usuario);
        } else {
            abrirPanelUsuario(usuario);
        }
    }

    private void abrirPanelAdministrador(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/MenuPrincipal.fxml"));
            Scene scene = new Scene(loader.load(), 1180, 720);
            MenuController controller = loader.getController();
            controller.inicializarUsuario(usuario);

            Stage stageActual = (Stage) txtUsuario.getScene().getWindow();
            stageActual.setTitle("EduVot - Panel de administrador");
            stageActual.setMinWidth(1180);
            stageActual.setMinHeight(720);
            stageActual.setScene(scene);
            stageActual.setMaximized(true);
            stageActual.show();

        } catch (IOException e) {
            lblMensaje.setText("No se pudo abrir el panel de administrador.");
            System.out.println("Error al abrir el panel de administrador: " + e.getMessage());
        }
    }

    private void abrirPanelUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Interfaz/PanelUsuario.fxml"));
            Scene scene = new Scene(loader.load(), 1180, 720);
            PanelUsuarioController controller = loader.getController();
            controller.inicializarUsuario(usuario);

            Stage stageActual = (Stage) txtUsuario.getScene().getWindow();
            stageActual.setTitle("EduVot - Zona de votacion");
            stageActual.setMinWidth(1180);
            stageActual.setMinHeight(720);
            stageActual.setScene(scene);
            stageActual.setMaximized(true);
            stageActual.show();

        } catch (IOException e) {
            lblMensaje.setText("No se pudo abrir la zona de votacion.");
            System.out.println("Error al abrir la zona de votacion: " + e.getMessage());
        }
    }

    @FXML
    private void alternarPasswordVisible() {
        passwordVisible = !passwordVisible;

        // JavaFX usa visible/managed para que el campo oculto no ocupe espacio.
        txtPassword.setVisible(!passwordVisible);
        txtPassword.setManaged(!passwordVisible);
        txtPasswordVisible.setVisible(passwordVisible);
        txtPasswordVisible.setManaged(passwordVisible);
        iconoPassword.setContent(passwordVisible
                ? "M2 4.27 3.28 3 21 20.72 19.73 22l-3.08-3.08c-1.44.37-3 .58-4.65.58-5 0-9.27-3.11-11-7.5.8-2.03 2.16-3.77 3.88-5.05L2 4.27zm7.53 7.53c-.02.07-.03.13-.03.2 0 1.38 1.12 2.5 2.5 2.5.07 0 .13-.01.2-.03L9.53 11.8zm2.31-4.29c.05 0 .11-.01.16-.01 2.76 0 5 2.24 5 5 0 .05-.01.11-.01.16L20.2 15.87c1.05-1.01 1.87-2.22 2.8-3.87-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.24-4 .67l3.84 3.84z"
                : "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zm0 12.5c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z");

        if (passwordVisible) {
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.requestFocus();
            txtPassword.positionCaret(txtPassword.getText().length());
        }
    }

    private void enfocarCampoPassword() {
        TextField campoActivo = passwordVisible ? txtPasswordVisible : txtPassword;
        campoActivo.requestFocus();
        campoActivo.positionCaret(campoActivo.getText().length());
    }

    private boolean esClickEnBotonPassword(Object target) {
        if (!(target instanceof Node)) {
            return false;
        }

        // Recorre los padres del nodo para saber si el click fue en el boton del ojo.
        Node nodo = (Node) target;
        while (nodo != null) {
            if (nodo == btnVerPassword) {
                return true;
            }
            nodo = nodo.getParent();
        }
        return false;
    }
}
