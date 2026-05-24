package com.proyecto.controladores;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.proyecto.dao.EncuestaDAO;
import com.proyecto.dao.UsuarioDAO;
import com.proyecto.modelos.Encuesta;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GestionEncuestasController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private DatePicker dpFechaFin;

    @FXML
    private ComboBox<String> cmbTipoEncuesta;

    @FXML
    private MenuButton btnSubcategorias;

    @FXML
    private CheckBox chkActiva;

    @FXML
    private TextField txtOpcion;

    @FXML
    private ListView<String> listaOpciones;

    @FXML
    private TextField txtBuscarEncuesta;

    @FXML
    private TableView<Encuesta> tablaEncuestas;

    @FXML
    private TableColumn<Encuesta, Integer> colId;

    @FXML
    private TableColumn<Encuesta, String> colTitulo;

    @FXML
    private TableColumn<Encuesta, String> colFechaInicio;

    @FXML
    private TableColumn<Encuesta, String> colFechaFin;

    @FXML
    private TableColumn<Encuesta, String> colTipo;

    @FXML
    private TableColumn<Encuesta, Boolean> colActiva;

    @FXML
    private TableColumn<Encuesta, Integer> colOpciones;

    @FXML
    private TableColumn<Encuesta, Integer> colVotos;

    private EncuestaDAO encuestaDAO;
    private UsuarioDAO usuarioDAO;
    private ObservableList<Encuesta> encuestas;
    private ObservableList<String> opciones;
    private MenuController menuController;
    private Usuario usuarioActual;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    @FXML
    public void initialize() {
        encuestaDAO = new EncuestaDAO();
        usuarioDAO = new UsuarioDAO();
        opciones = FXCollections.observableArrayList();
        listaOpciones.setItems(opciones);
        txtOpcion.setOnAction(event -> agregarOpcion());

        cmbTipoEncuesta.setItems(FXCollections.observableArrayList("UNA_OPCION", "VARIAS_OPCIONES"));
        cmbTipoEncuesta.setValue("UNA_OPCION");
        chkActiva.setSelected(true);

        configurarDesplegableSubcategorias();
        configurarTabla();
        configurarSeleccionTabla();
        configurarBusqueda();
        cargarEncuestas();
    }

    @FXML
    private void agregarEncuesta() {
        if (tablaEncuestas.getSelectionModel().getSelectedItem() != null) {
            mostrarAlerta("Encuesta ya seleccionada", "Para guardar cambios de una encuesta existente debes pulsar Editar. Usa Limpiar si quieres crear una nueva.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Encuesta nueva = construirEncuesta(0);

        if (encuestaDAO.insertarEncuesta(nueva, List.copyOf(opciones), obtenerSubcategoriasSeleccionadas())) {
            mostrarAlerta("Exito", "Encuesta anadida correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo anadir la encuesta.\n\nDetalle: " + obtenerDetalleError());
        }
    }

    @FXML
    private void editarEncuesta() {
        Encuesta seleccionada = tablaEncuestas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atencion", "Debes seleccionar una encuesta para editar.");
            return;
        }

        if (encuestaDAO.contarVotosEncuesta(seleccionada.getIdEncuesta()) > 0) {
            mostrarAlerta("Encuesta con votos", "No se puede editar una encuesta que ya tiene votos registrados.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Encuesta actualizada = construirEncuesta(seleccionada.getIdEncuesta());

        if (encuestaDAO.actualizarEncuesta(actualizada, List.copyOf(opciones), obtenerSubcategoriasSeleccionadas())) {
            mostrarAlerta("Exito", "Encuesta actualizada correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la encuesta.\n\nDetalle: " + obtenerDetalleError());
        }
    }

    @FXML
    private void eliminarEncuesta() {
        Encuesta seleccionada = tablaEncuestas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atencion", "Debes seleccionar una encuesta para eliminar.");
            return;
        }

        if (encuestaDAO.contarVotosEncuesta(seleccionada.getIdEncuesta()) > 0) {
            mostrarAlerta("Encuesta con votos", "No se puede eliminar una encuesta que ya tiene votos registrados.");
            return;
        }

        if (encuestaDAO.eliminarEncuesta(seleccionada.getIdEncuesta())) {
            mostrarAlerta("Exito", "Encuesta eliminada correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar la encuesta.\n\nDetalle: " + obtenerDetalleError());
        }
    }

    @FXML
    private void agregarOpcion() {
        String opcion = txtOpcion.getText().trim();
        if (opcion.isEmpty()) {
            mostrarAlerta("Campo vacio", "Escribe el texto de la opcion.");
            return;
        }

        if (opciones.stream().anyMatch(existente -> existente.equalsIgnoreCase(opcion))) {
            mostrarAlerta("Opcion duplicada", "Esa opcion ya esta en la lista.");
            return;
        }

        opciones.add(opcion);
        txtOpcion.clear();
    }

    @FXML
    private void quitarOpcion() {
        String seleccionada = listaOpciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atencion", "Selecciona una opcion para quitar.");
            return;
        }
        opciones.remove(seleccionada);
    }

    @FXML
    private void limpiarCampos() {
        txtTitulo.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cmbTipoEncuesta.setValue("UNA_OPCION");
        chkActiva.setSelected(true);
        opciones.clear();
        txtOpcion.clear();
        seleccionarSubcategorias(List.of());
        tablaEncuestas.getSelectionModel().clearSelection();
    }

    @FXML
    private void buscarEncuestas() {
        String busqueda = txtBuscarEncuesta.getText().trim();
        if (busqueda.isEmpty()) {
            cargarEncuestas();
            return;
        }
        cargarTabla(encuestaDAO.buscarEncuestas(busqueda));
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscarEncuesta.clear();
        cargarEncuestas();
    }

    @FXML
    private void volverPanelAdministrador() {
        if (menuController != null) {
            menuController.mostrarDashboard();
            return;
        }

        Stage stageActual = (Stage) tablaEncuestas.getScene().getWindow();
        stageActual.close();
    }

    private void configurarTabla() {
        // Enlaza cada columna de la tabla con una propiedad calculada o directa de Encuesta.
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdEncuesta()).asObject());
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colFechaInicio.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaInicio().format(FORMATO_FECHA)));
        colFechaFin.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaFin().format(FORMATO_FECHA)));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoEncuesta()));
        colActiva.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActiva()));
        colOpciones.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalOpciones()).asObject());
        colVotos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalVotos()).asObject());
    }

    private void configurarSeleccionTabla() {
        tablaEncuestas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                // La tabla es tambien el punto de entrada para editar: rellena formulario,
                // opciones y colectivos destinatarios con lo guardado en base de datos.
                txtTitulo.setText(seleccionada.getTitulo());
                txtDescripcion.setText(seleccionada.getDescripcion());
                dpFechaInicio.setValue(seleccionada.getFechaInicio().toLocalDate());
                dpFechaFin.setValue(seleccionada.getFechaFin().toLocalDate());
                cmbTipoEncuesta.setValue(seleccionada.getTipoEncuesta());
                chkActiva.setSelected(seleccionada.isActiva());
                opciones.setAll(encuestaDAO.obtenerOpcionesEncuesta(seleccionada.getIdEncuesta()));
                seleccionarSubcategorias(encuestaDAO.obtenerSubcategoriasEncuesta(seleccionada.getIdEncuesta()));
            }
        });
    }

    private void configurarBusqueda() {
        txtBuscarEncuesta.setOnAction(event -> buscarEncuestas());
        txtBuscarEncuesta.textProperty().addListener((obs, antes, ahora) -> {
            if (ahora.trim().isEmpty()) {
                cargarEncuestas();
            } else if (ahora.trim().length() >= 2) {
                buscarEncuestas();
            }
        });
    }

    private void configurarDesplegableSubcategorias() {
        btnSubcategorias.getItems().clear();

        // Se construye dinamicamente para que las nuevas subcategorias aparezcan sin tocar el FXML.
        for (String subcategoria : usuarioDAO.obtenerSubcategorias()) {
            CheckMenuItem item = new CheckMenuItem(subcategoria);
            item.selectedProperty().addListener((obs, antes, ahora) -> actualizarTextoSubcategorias());
            btnSubcategorias.getItems().add(item);
        }

        actualizarTextoSubcategorias();
    }

    private Encuesta construirEncuesta(int idEncuesta) {
        int idCreador = usuarioActual == null ? 1 : usuarioActual.getIdUsuario();
        // La fecha fin se guarda al final del dia para que siga abierta durante toda la jornada elegida.
        return new Encuesta(
                idEncuesta,
                txtTitulo.getText().trim(),
                txtDescripcion.getText().trim(),
                dpFechaInicio.getValue().atStartOfDay(),
                dpFechaFin.getValue().atTime(23, 59),
                chkActiva.isSelected(),
                cmbTipoEncuesta.getValue(),
                idCreador,
                opciones.size(),
                0
        );
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty()) {
            mostrarAlerta("Campo vacio", "El titulo de la encuesta es obligatorio.");
            return false;
        }

        if (dpFechaInicio.getValue() == null || dpFechaFin.getValue() == null) {
            mostrarAlerta("Fechas obligatorias", "Debes seleccionar fecha de inicio y fecha de fin.");
            return false;
        }

        if (dpFechaFin.getValue().isBefore(dpFechaInicio.getValue())) {
            mostrarAlerta("Fechas incorrectas", "La fecha de fin no puede ser anterior a la fecha de inicio.");
            return false;
        }

        if (obtenerSubcategoriasSeleccionadas().isEmpty()) {
            mostrarAlerta("Subcategoria obligatoria", "Selecciona al menos una subcategoria destinataria.");
            return false;
        }

        if (opciones.size() < 2) {
            mostrarAlerta("Opciones insuficientes", "La encuesta debe tener al menos dos opciones de voto.");
            return false;
        }

        return true;
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
        List<String> normalizadas = subcategoriasGuardadas.stream()
                .map(subcategoria -> subcategoria.toLowerCase().trim())
                .toList();

        for (javafx.scene.control.MenuItem menuItem : btnSubcategorias.getItems()) {
            if (menuItem instanceof CheckMenuItem item) {
                item.setSelected(normalizadas.contains(item.getText().toLowerCase().trim()));
            }
        }

        actualizarTextoSubcategorias();
    }

    private void actualizarTextoSubcategorias() {
        List<String> seleccionadas = obtenerSubcategoriasSeleccionadas();
        btnSubcategorias.setText(seleccionadas.isEmpty() ? "Seleccionar subcategorias" : String.join(", ", seleccionadas));
    }

    private void cargarEncuestas() {
        cargarTabla(encuestaDAO.obtenerEncuestas());
    }

    private void cargarTabla(List<Encuesta> encuestasEncontradas) {
        encuestas = FXCollections.observableArrayList(encuestasEncontradas);
        tablaEncuestas.setItems(encuestas);
    }

    private void refrescarBusquedaActual() {
        if (txtBuscarEncuesta.getText().trim().isEmpty()) {
            cargarEncuestas();
        } else {
            buscarEncuestas();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String obtenerDetalleError() {
        String detalle = encuestaDAO.getUltimoError();
        return detalle == null || detalle.isBlank() ? "No hay detalle disponible." : detalle;
    }
}
