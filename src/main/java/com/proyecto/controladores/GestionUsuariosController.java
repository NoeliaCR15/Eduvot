package com.proyecto.controladores;

import java.util.List;

import com.proyecto.dao.UsuarioDAO;
import com.proyecto.modelos.Usuario;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GestionUsuariosController {

    @FXML
    private TextField txtDni;

    @FXML
    private TextField txtNombreUsuario;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtBuscarUsuario;

    @FXML
    private ComboBox<String> cmbCurso;

    @FXML
    private MenuButton btnSubcategorias;

    @FXML
    private VBox contenedorFamilia;

    @FXML
    private MenuButton btnAlumnosFamilia;

    @FXML
    private CheckBox chkAdministrador;

    @FXML
    private CheckBox chkActivo;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private TableColumn<Usuario, Integer> colId;

    @FXML
    private TableColumn<Usuario, String> colDni;

    @FXML
    private TableColumn<Usuario, String> colNombreUsuario;

    @FXML
    private TableColumn<Usuario, String> colGrupo;

    @FXML
    private TableColumn<Usuario, Boolean> colAdministrador;

    @FXML
    private TableColumn<Usuario, Boolean> colActivo;

    private UsuarioDAO usuarioDAO;
    private ObservableList<Usuario> listaUsuarios;
    private MenuController menuController;
    private boolean cargandoAlumnosFamilia;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }
    private static final String[] CURSOS = {"1ESO", "2ESO", "3ESO", "4ESO", "1BACH", "2BACH", "1DAM", "2DAM"};

    @FXML
    public void initialize() {
        usuarioDAO = new UsuarioDAO();
        configurarCursos();
        configurarDesplegableSubcategorias();
        configurarDesplegableAlumnosFamilia();

        // Enlaza cada columna de la tabla con una propiedad del modelo Usuario.
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdUsuario()).asObject());
        colDni.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDni()));
        colNombreUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreUsuario()));
        colGrupo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrupo()));
        colAdministrador.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isEsAdministrador()));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()));

        limpiarBusqueda();
        configurarSeleccionTabla();
        configurarBusqueda();
    }

    private void configurarSeleccionTabla() {
        // Al seleccionar un usuario, se cargan tambien sus subcategorias y alumnos vinculados.
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                txtDni.setText(seleccionado.getDni());
                txtNombreUsuario.setText(seleccionado.getNombreUsuario());
                txtPassword.setText(seleccionado.getPassword());
                cmbCurso.setValue(seleccionado.getGrupo());
                seleccionarSubcategorias(usuarioDAO.obtenerSubcategoriasUsuario(seleccionado.getIdUsuario()));
                seleccionarAlumnosFamilia(usuarioDAO.obtenerAlumnosFamilia(seleccionado.getIdUsuario()));
                chkAdministrador.setSelected(seleccionado.isEsAdministrador());
                chkActivo.setSelected(seleccionado.isActivo());
            }
        });
    }

    @FXML
    private void agregarUsuario() {
        // Evita duplicados por error cuando el formulario esta cargado desde una fila existente.
        if (tablaUsuarios.getSelectionModel().getSelectedItem() != null) {
            mostrarAlerta("Usuario ya seleccionado", "Para guardar cambios de un usuario existente debes pulsar Editar. Usa Limpiar si quieres crear un usuario nuevo.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Usuario nuevo = new Usuario(
                txtDni.getText().trim(),
                txtNombreUsuario.getText().trim(),
                txtPassword.getText().trim(),
                chkAdministrador.isSelected(),
                chkActivo.isSelected(),
                obtenerCursoSeleccionado()
        );

        if (usuarioDAO.insertarUsuario(nuevo)) {
            usuarioDAO.guardarSubcategoriasUsuario(nuevo.getIdUsuario(), obtenerSubcategoriasSeleccionadas());
            usuarioDAO.guardarAlumnosFamilia(nuevo.getIdUsuario(), obtenerIdsAlumnosFamiliaSeleccionados());
            mostrarAlerta("Exito", "Usuario anadido correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo anadir el usuario.");
        }
    }

    @FXML
    private void editarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atencion", "Debes seleccionar un usuario para editar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Usuario actualizado = new Usuario(
                seleccionado.getIdUsuario(),
                txtDni.getText().trim(),
                txtNombreUsuario.getText().trim(),
                txtPassword.getText().trim(),
                chkAdministrador.isSelected(),
                chkActivo.isSelected(),
                obtenerCursoSeleccionado()
        );

        if (usuarioDAO.actualizarUsuario(actualizado)) {
            usuarioDAO.guardarSubcategoriasUsuario(actualizado.getIdUsuario(), obtenerSubcategoriasSeleccionadas());
            usuarioDAO.guardarAlumnosFamilia(actualizado.getIdUsuario(), obtenerIdsAlumnosFamiliaSeleccionados());
            mostrarAlerta("Exito", "Usuario actualizado correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar el usuario.");
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atencion", "Debes seleccionar un usuario para eliminar.");
            return;
        }

        if (usuarioDAO.eliminarUsuario(seleccionado.getIdUsuario())) {
            mostrarAlerta("Exito", "Usuario eliminado correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar el usuario.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtDni.clear();
        txtNombreUsuario.clear();
        txtPassword.clear();
        cmbCurso.setValue(null);
        cmbCurso.getEditor().clear();
        seleccionarSubcategorias(List.of());
        seleccionarAlumnosFamilia(List.of());
        chkAdministrador.setSelected(false);
        chkActivo.setSelected(true);
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void volverPanelAdministrador() {
        if (menuController != null) {
            menuController.mostrarDashboard();
            return;
        }

        Stage stageActual = (Stage) tablaUsuarios.getScene().getWindow();
        stageActual.close();
    }

    @FXML
    private void buscarUsuarios() {
        String busqueda = txtBuscarUsuario.getText().trim();

        if (busqueda.isEmpty()) {
            limpiarBusqueda();
            return;
        }

        List<Usuario> usuarios = usuarioDAO.buscarUsuarios(busqueda);
        listaUsuarios = FXCollections.observableArrayList(usuarios);
        tablaUsuarios.setItems(listaUsuarios);
    }

    @FXML
    private void limpiarBusqueda() {
        if (txtBuscarUsuario != null) {
            txtBuscarUsuario.clear();
        }
        limpiarResultadosBusqueda();
    }

    private void limpiarResultadosBusqueda() {
        listaUsuarios = FXCollections.observableArrayList();
        tablaUsuarios.setItems(listaUsuarios);
    }

    private boolean validarCampos() {
        if (txtDni.getText().trim().isEmpty()
                || txtNombreUsuario.getText().trim().isEmpty()
                || txtPassword.getText().trim().isEmpty()) {
            mostrarAlerta("Campos vacios", "DNI, nombre de usuario y contrasena son obligatorios.");
            return false;
        }

        if (obtenerSubcategoriasSeleccionadas().isEmpty()) {
            mostrarAlerta("Subcategoria obligatoria", "Debes seleccionar al menos una subcategoria.");
            return false;
        }

        if (esSubcategoriaFamiliarSeleccionada() && obtenerIdsAlumnosFamiliaSeleccionados().isEmpty()) {
            mostrarAlerta("Alumno obligatorio", "Si el usuario pertenece a Familia o tutor, debes seleccionar al menos un alumno vinculado.");
            return false;
        }

        return true;
    }

    private void configurarCursos() {
        cmbCurso.setItems(FXCollections.observableArrayList(CURSOS));
    }

    private void configurarBusqueda() {
        txtBuscarUsuario.setOnAction(event -> buscarUsuarios());
        txtBuscarUsuario.textProperty().addListener((obs, antes, ahora) -> {
            if (ahora.trim().isEmpty()) {
                limpiarResultadosBusqueda();
            } else if (ahora.trim().length() >= 2) {
                buscarUsuarios();
            }
        });
    }

    private void refrescarBusquedaActual() {
        if (txtBuscarUsuario.getText().trim().isEmpty()) {
            limpiarBusqueda();
        } else {
            buscarUsuarios();
        }
    }

    private void configurarDesplegableSubcategorias() {
        btnSubcategorias.getItems().clear();

        List<String> subcategorias = usuarioDAO.obtenerSubcategorias();
        if (subcategorias.isEmpty()) {
            subcategorias = List.of("Alumnado", "Profesorado", "Familia o tutor");
        }

        for (String subcategoria : subcategorias) {
            CheckMenuItem item = new CheckMenuItem(subcategoria);
            item.selectedProperty().addListener((obs, antes, ahora) -> {
                actualizarTextoSubcategorias();
                actualizarVistaFamilia();
            });
            btnSubcategorias.getItems().add(item);
        }
        actualizarTextoSubcategorias();
        actualizarVistaFamilia();
    }

    private void configurarDesplegableAlumnosFamilia() {
        List<Integer> idsSeleccionados = obtenerIdsAlumnosFamiliaSeleccionados();
        // Bandera para evitar recargas encadenadas mientras se reconstruye el desplegable.
        cargandoAlumnosFamilia = true;
        btnAlumnosFamilia.getItems().clear();

        for (Usuario alumno : usuarioDAO.obtenerAlumnos()) {
            CheckMenuItem item = new CheckMenuItem(alumno.toString());
            item.setUserData(alumno);
            item.setSelected(idsSeleccionados.contains(alumno.getIdUsuario()));
            item.selectedProperty().addListener((obs, antes, ahora) -> actualizarTextoAlumnosFamilia());
            btnAlumnosFamilia.getItems().add(item);
        }

        cargandoAlumnosFamilia = false;
        actualizarTextoAlumnosFamilia();
    }

    private String obtenerCursoSeleccionado() {
        String curso = cmbCurso.getEditor().getText();
        if (curso == null || curso.trim().isEmpty()) {
            curso = cmbCurso.getValue();
        }
        return curso == null ? "" : curso.trim();
    }

    private List<String> obtenerSubcategoriasSeleccionadas() {
        return btnSubcategorias.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem)
                .map(item -> (CheckMenuItem) item)
                .filter(CheckMenuItem::isSelected)
                .map(CheckMenuItem::getText)
                .toList();
    }

    private void seleccionarSubcategorias(List<String> subcategoriasGuardadas) {
        List<String> subcategoriasNormalizadas = subcategoriasGuardadas.stream()
                .map(String::toLowerCase)
                .toList();

        for (javafx.scene.control.MenuItem menuItem : btnSubcategorias.getItems()) {
            if (menuItem instanceof CheckMenuItem item) {
                item.setSelected(subcategoriasNormalizadas.contains(item.getText().toLowerCase()));
            }
        }

        actualizarTextoSubcategorias();
        actualizarVistaFamilia();
    }

    private void actualizarTextoSubcategorias() {
        List<String> subcategorias = obtenerSubcategoriasSeleccionadas();
        btnSubcategorias.setText(subcategorias.isEmpty() ? "Seleccionar subcategorias" : String.join(", ", subcategorias));
    }

    private boolean esSubcategoriaFamiliarSeleccionada() {
        // Permite pequenas variaciones en el nombre de la subcategoria familiar.
        return obtenerSubcategoriasSeleccionadas().stream()
                .map(subcategoria -> subcategoria.toLowerCase().trim())
                .anyMatch(subcategoria -> subcategoria.contains("famil")
                        || subcategoria.contains("padre")
                        || subcategoria.contains("madre")
                        || subcategoria.contains("tutor"));
    }

    private void actualizarVistaFamilia() {
        boolean mostrarFamilia = esSubcategoriaFamiliarSeleccionada();
        contenedorFamilia.setVisible(mostrarFamilia);
        contenedorFamilia.setManaged(mostrarFamilia);

        // Si el usuario deja de ser familia/tutor, se limpian relaciones para no guardar datos incoherentes.
        if (mostrarFamilia && !cargandoAlumnosFamilia) {
            configurarDesplegableAlumnosFamilia();
        } else if (!mostrarFamilia) {
            seleccionarAlumnosFamilia(List.of());
        }
    }

    private List<Integer> obtenerIdsAlumnosFamiliaSeleccionados() {
        return btnAlumnosFamilia.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem)
                .map(item -> (CheckMenuItem) item)
                .filter(CheckMenuItem::isSelected)
                .map(CheckMenuItem::getUserData)
                .filter(data -> data instanceof Usuario)
                .map(data -> ((Usuario) data).getIdUsuario())
                .toList();
    }

    private void seleccionarAlumnosFamilia(List<Usuario> alumnosGuardados) {
        List<Integer> idsAlumnosGuardados = alumnosGuardados.stream()
                .map(Usuario::getIdUsuario)
                .toList();

        for (javafx.scene.control.MenuItem menuItem : btnAlumnosFamilia.getItems()) {
            if (menuItem instanceof CheckMenuItem item && item.getUserData() instanceof Usuario alumno) {
                item.setSelected(idsAlumnosGuardados.contains(alumno.getIdUsuario()));
            }
        }

        actualizarTextoAlumnosFamilia();
    }

    private void actualizarTextoAlumnosFamilia() {
        List<String> alumnos = btnAlumnosFamilia.getItems().stream()
                .filter(item -> item instanceof CheckMenuItem)
                .map(item -> (CheckMenuItem) item)
                .filter(CheckMenuItem::isSelected)
                .map(CheckMenuItem::getText)
                .toList();

        if (btnAlumnosFamilia.getItems().isEmpty()) {
            btnAlumnosFamilia.setText("No hay alumnos disponibles");
        } else {
            btnAlumnosFamilia.setText(alumnos.isEmpty() ? "Seleccionar alumno/s" : String.join(", ", alumnos));
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
