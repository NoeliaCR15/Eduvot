package com.proyecto.controladores;

import java.util.List;

import com.proyecto.dao.SubcategoriaDAO;
import com.proyecto.modelos.Subcategoria;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GestionSubcategoriasController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtBuscarSubcategoria;

    @FXML
    private TableView<Subcategoria> tablaSubcategorias;

    @FXML
    private TableColumn<Subcategoria, Integer> colId;

    @FXML
    private TableColumn<Subcategoria, String> colNombre;

    @FXML
    private TableColumn<Subcategoria, Integer> colUsuariosAsociados;

    private SubcategoriaDAO subcategoriaDAO;
    private ObservableList<Subcategoria> listaSubcategorias;
    private MenuController menuController;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    public void initialize() {
        subcategoriaDAO = new SubcategoriaDAO();

        // Enlaza cada columna de la tabla con una propiedad del modelo Subcategoria.
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdSubcategoria()).asObject());
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colUsuariosAsociados.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getUsuariosAsociados()).asObject());

        configurarSeleccionTabla();
        configurarBusqueda();
        cargarSubcategorias();
    }

    @FXML
    private void agregarSubcategoria() {
        if (!validarCampos()) {
            return;
        }

        Subcategoria nueva = new Subcategoria(txtNombre.getText().trim());

        if (subcategoriaDAO.insertarSubcategoria(nueva)) {
            mostrarAlerta("Exito", "Subcategoria anadida correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo anadir la subcategoria. Revisa que no exista ya.");
        }
    }

    @FXML
    private void editarSubcategoria() {
        Subcategoria seleccionada = tablaSubcategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atencion", "Debes seleccionar una subcategoria para editar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        Subcategoria actualizada = new Subcategoria(
                seleccionada.getIdSubcategoria(),
                txtNombre.getText().trim(),
                seleccionada.getUsuariosAsociados()
        );

        if (subcategoriaDAO.actualizarSubcategoria(actualizada)) {
            mostrarAlerta("Exito", "Subcategoria actualizada correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo actualizar la subcategoria. Revisa que no exista ya.");
        }
    }

    @FXML
    private void eliminarSubcategoria() {
        Subcategoria seleccionada = tablaSubcategorias.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atencion", "Debes seleccionar una subcategoria para eliminar.");
            return;
        }

        // Evita borrar subcategorias que ya se usan en usuarios.
        int usuariosAsociados = subcategoriaDAO.contarUsuariosAsociados(seleccionada.getIdSubcategoria());
        if (usuariosAsociados > 0) {
            mostrarAlerta(
                    "Subcategoria en uso",
                    "No se puede eliminar porque esta asociada a " + usuariosAsociados + " usuario(s)."
            );
            return;
        }

        if (subcategoriaDAO.eliminarSubcategoria(seleccionada.getIdSubcategoria())) {
            mostrarAlerta("Exito", "Subcategoria eliminada correctamente.");
            refrescarBusquedaActual();
            limpiarCampos();
        } else {
            mostrarAlerta("Error", "No se pudo eliminar la subcategoria.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        tablaSubcategorias.getSelectionModel().clearSelection();
    }

    @FXML
    private void buscarSubcategorias() {
        String busqueda = txtBuscarSubcategoria.getText().trim();

        if (busqueda.isEmpty()) {
            cargarSubcategorias();
            return;
        }

        cargarTabla(subcategoriaDAO.buscarSubcategorias(busqueda));
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscarSubcategoria.clear();
        cargarSubcategorias();
    }

    @FXML
    private void volverPanelAdministrador() {
        if (menuController != null) {
            menuController.mostrarDashboard();
            return;
        }

        Stage stageActual = (Stage) tablaSubcategorias.getScene().getWindow();
        stageActual.close();
    }

    private void configurarSeleccionTabla() {
        // Al seleccionar una fila, carga su nombre en el formulario para poder editarlo.
        tablaSubcategorias.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                txtNombre.setText(seleccionada.getNombre());
            }
        });
    }

    private void configurarBusqueda() {
        txtBuscarSubcategoria.setOnAction(event -> buscarSubcategorias());
        // Busca automaticamente a partir de 2 caracteres y restaura la tabla si se vacia el campo.
        txtBuscarSubcategoria.textProperty().addListener((obs, antes, ahora) -> {
            if (ahora.trim().isEmpty()) {
                cargarSubcategorias();
            } else if (ahora.trim().length() >= 2) {
                buscarSubcategorias();
            }
        });
    }

    private void cargarSubcategorias() {
        cargarTabla(subcategoriaDAO.obtenerSubcategorias());
    }

    private void cargarTabla(List<Subcategoria> subcategorias) {
        listaSubcategorias = FXCollections.observableArrayList(subcategorias);
        tablaSubcategorias.setItems(listaSubcategorias);
    }

    private void refrescarBusquedaActual() {
        if (txtBuscarSubcategoria.getText().trim().isEmpty()) {
            cargarSubcategorias();
        } else {
            buscarSubcategorias();
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campo vacio", "El nombre de la subcategoria es obligatorio.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
