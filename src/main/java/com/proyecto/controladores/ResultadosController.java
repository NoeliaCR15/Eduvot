package com.proyecto.controladores;

import java.time.LocalDateTime;
import java.util.List;

import com.proyecto.dao.EncuestaDAO;
import com.proyecto.modelos.Encuesta;
import com.proyecto.modelos.ResultadoOpcion;
import com.proyecto.modelos.Usuario;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ResultadosController {

    @FXML
    private TableView<Encuesta> tablaEncuestas;

    @FXML
    private Label lblListadoEncuestas;

    @FXML
    private TableColumn<Encuesta, Integer> colId;

    @FXML
    private TableColumn<Encuesta, String> colTitulo;

    @FXML
    private TableColumn<Encuesta, Boolean> colActiva;

    @FXML
    private TableColumn<Encuesta, Integer> colVotos;

    @FXML
    private Label lblEncuestaSeleccionada;

    @FXML
    private Label lblTotalVotos;

    @FXML
    private Label lblDestinatarios;

    @FXML
    private Label lblParticipacion;

    @FXML
    private TableView<ResultadoOpcion> tablaResultados;

    @FXML
    private TableColumn<ResultadoOpcion, String> colOpcion;

    @FXML
    private TableColumn<ResultadoOpcion, Integer> colTotalOpcion;

    @FXML
    private BarChart<String, Number> graficoResultados;

    @FXML
    private CategoryAxis ejeOpciones;

    @FXML
    private NumberAxis ejeVotos;

    private EncuestaDAO encuestaDAO;
    private MenuController menuController;
    private Usuario usuarioActual;
    private boolean mostrandoArchivadas;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    @FXML
    public void initialize() {
        encuestaDAO = new EncuestaDAO();
        configurarTablas();
        cargarEncuestas();
    }

    @FXML
    private void mostrarEncuestasActivas() {
        mostrandoArchivadas = false;
        cargarEncuestas();
    }

    @FXML
    private void mostrarEncuestasArchivadas() {
        mostrandoArchivadas = true;
        cargarEncuestas();
    }

    @FXML
    private void archivarEncuestaSeleccionada() {
        Encuesta seleccionada = tablaEncuestas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Atencion", "Debes seleccionar una encuesta para archivar.");
            return;
        }

        if (mostrandoArchivadas || encuestaDAO.encuestaArchivada(seleccionada.getIdEncuesta())) {
            mostrarAlerta("Encuesta archivada", "Esta encuesta ya esta archivada.");
            return;
        }

        if (seleccionada.getFechaFin().isAfter(LocalDateTime.now())) {
            mostrarAlerta("Encuesta abierta", "Solo se pueden archivar encuestas que ya han finalizado.");
            return;
        }

        // Archivar conserva resultados y votos, pero saca la encuesta de los listados operativos.
        int idUsuario = usuarioActual == null ? 1 : usuarioActual.getIdUsuario();
        if (encuestaDAO.archivarEncuesta(seleccionada.getIdEncuesta(), idUsuario, "Encuesta finalizada")) {
            mostrarAlerta("Exito", "Encuesta archivada correctamente.");
            cargarEncuestas();
            limpiarResultados();
        } else {
            mostrarAlerta("Error", "No se pudo archivar la encuesta.\n\nDetalle: " + obtenerDetalleError());
        }
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

    private void configurarTablas() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getIdEncuesta()).asObject());
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colActiva.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActiva()));
        colVotos.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalVotos()).asObject());

        colOpcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTextoOpcion()));
        colTotalOpcion.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getTotalVotos()).asObject());

        tablaEncuestas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionada) -> {
            if (seleccionada != null) {
                mostrarResultados(seleccionada);
            }
        });
    }

    private void cargarEncuestas() {
        lblListadoEncuestas.setText(mostrandoArchivadas ? "Encuestas archivadas" : "Encuestas disponibles");
        // El mismo panel sirve para revisar trabajo activo y consultar historico archivado.
        List<Encuesta> encuestas = mostrandoArchivadas
                ? encuestaDAO.obtenerEncuestasArchivadas()
                : encuestaDAO.obtenerEncuestas();

        tablaEncuestas.setItems(FXCollections.observableArrayList(encuestas));
        if (!tablaEncuestas.getItems().isEmpty()) {
            tablaEncuestas.getSelectionModel().selectFirst();
        } else {
            limpiarResultados();
        }
    }

    private void mostrarResultados(Encuesta encuesta) {
        List<ResultadoOpcion> resultados = encuestaDAO.obtenerResultadosEncuesta(encuesta.getIdEncuesta());
        int destinatarios = encuestaDAO.contarVotantesDestinatariosEncuesta(encuesta.getIdEncuesta());
        int totalVotos = encuestaDAO.contarVotosEncuesta(encuesta.getIdEncuesta());
        // Participacion compara votos emitidos con usuarios activos destinatarios de la encuesta.
        double porcentaje = destinatarios == 0 ? 0 : (totalVotos * 100.0 / destinatarios);

        lblEncuestaSeleccionada.setText(encuesta.getTitulo());
        lblTotalVotos.setText(String.valueOf(totalVotos));
        lblDestinatarios.setText(String.valueOf(destinatarios));
        lblParticipacion.setText(String.format("%.1f%%", porcentaje));

        tablaResultados.setItems(FXCollections.observableArrayList(resultados));
        actualizarGrafico(resultados);
    }

    private void actualizarGrafico(List<ResultadoOpcion> resultados) {
        graficoResultados.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Votos");

        for (ResultadoOpcion resultado : resultados) {
            serie.getData().add(new XYChart.Data<>(resultado.getTextoOpcion(), resultado.getTotalVotos()));
        }

        graficoResultados.getData().add(serie);
        ejeVotos.setLabel("Votos");
        ejeOpciones.setLabel("Opciones");
    }

    private void limpiarResultados() {
        lblEncuestaSeleccionada.setText("Selecciona una encuesta");
        lblTotalVotos.setText("0");
        lblDestinatarios.setText("0");
        lblParticipacion.setText("0%");
        tablaResultados.getItems().clear();
        graficoResultados.getData().clear();
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
