package com.proyecto.controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.dao.EncuestaDAO;
import com.proyecto.modelos.Encuesta;
import com.proyecto.modelos.OpcionVoto;
import com.proyecto.modelos.ParticipacionUsuario;
import com.proyecto.modelos.Usuario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private Usuario usuarioActual;
    private EncuestaDAO encuestaDAO;

    @FXML
    public void initialize() {
        // Conserva la portada del usuario para volver desde modulos internos.
        contenidoDashboard = new ArrayList<>(contenidoUsuario.getChildren());
        encuestaDAO = new EncuestaDAO();
    }

    public void inicializarUsuario(Usuario usuario) {
        usuarioActual = usuario;
        lblNombreUsuario.setText(usuario.getNombreUsuario());
        lblGrupoUsuario.setText("Curso: " + usuario.getGrupo());
    }

    @FXML
    private void abrirVotaciones() {
        mostrarVotacionesDisponibles();
    }

    @FXML
    private void abrirMisVotos() {
        mostrarMiParticipacion();
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

    private void mostrarVotacionesDisponibles() {
        HBox cabecera = crearCabeceraInterna(
                "Votaciones disponibles",
                "Encuestas activas para tus subcategorias y pendientes de votar.");

        VBox listado = new VBox(14);
        listado.getStyleClass().add("table-card");
        listado.setPadding(new Insets(22));

        List<Encuesta> encuestas = usuarioActual == null
                ? List.of()
                : encuestaDAO.obtenerEncuestasDisponiblesUsuario(usuarioActual.getIdUsuario());

        Label tituloListado = new Label("Encuestas abiertas");
        tituloListado.getStyleClass().add("section-title");
        listado.getChildren().add(tituloListado);

        if (encuestas.isEmpty()) {
            Label sinEncuestas = new Label("No tienes votaciones disponibles ahora mismo.");
            sinEncuestas.getStyleClass().add("module-text");
            listado.getChildren().add(sinEncuestas);
        } else {
            for (Encuesta encuesta : encuestas) {
                listado.getChildren().add(crearTarjetaEncuesta(encuesta));
            }
        }

        ScrollPane scrollPane = new ScrollPane(listado);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        contenidoUsuario.getChildren().setAll(cabecera, scrollPane);
    }

    private HBox crearCabeceraInterna(String titulo, String mensaje) {
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
        HBox.setHgrow(separador, Priority.ALWAYS);

        Button volver = new Button("Volver");
        volver.getStyleClass().add("secondary-button");
        volver.setOnAction(event -> volverDashboard());

        cabecera.getChildren().addAll(textos, separador, volver);
        return cabecera;
    }

    private VBox crearTarjetaEncuesta(Encuesta encuesta) {
        VBox tarjeta = new VBox(10);
        tarjeta.getStyleClass().add("module-card");
        tarjeta.setPadding(new Insets(18));

        HBox filaTitulo = new HBox(14);
        filaTitulo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox textos = new VBox(6);
        Label titulo = new Label(encuesta.getTitulo());
        titulo.getStyleClass().add("module-title");
        titulo.setWrapText(true);
        Label descripcion = new Label(encuesta.getDescripcion() == null || encuesta.getDescripcion().isBlank()
                ? "Sin descripcion."
                : encuesta.getDescripcion());
        descripcion.getStyleClass().add("module-text");
        descripcion.setWrapText(true);
        textos.getChildren().addAll(titulo, descripcion);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Button verDetalle = new Button("Ver opciones");
        verDetalle.getStyleClass().add("module-button");
        verDetalle.setOnAction(event -> mostrarDetalleEncuesta(encuesta));

        filaTitulo.getChildren().addAll(textos, verDetalle);

        Label datos = new Label("Hasta: " + encuesta.getFechaFin().toLocalDate()
                + "   |   Tipo: " + encuesta.getTipoEncuesta()
                + "   |   Opciones: " + encuesta.getTotalOpciones());
        datos.getStyleClass().add("info-text");

        tarjeta.getChildren().addAll(filaTitulo, datos);
        return tarjeta;
    }

    private void mostrarDetalleEncuesta(Encuesta encuesta) {
        HBox cabecera = crearCabeceraInterna(
                encuesta.getTitulo(),
                "Opciones disponibles para esta votacion.");

        VBox detalle = new VBox(14);
        detalle.getStyleClass().add("table-card");
        detalle.setPadding(new Insets(22));

        Label descripcion = new Label(encuesta.getDescripcion() == null || encuesta.getDescripcion().isBlank()
                ? "Sin descripcion."
                : encuesta.getDescripcion());
        descripcion.getStyleClass().add("module-text");
        descripcion.setWrapText(true);

        Label tituloOpciones = new Label("Opciones de voto");
        tituloOpciones.getStyleClass().add("section-title");

        VBox opciones = new VBox(8);
        List<OpcionVoto> opcionesVoto = encuestaDAO.obtenerOpcionesVotoEncuesta(encuesta.getIdEncuesta());

        // El tipo de encuesta define el control visual y evita selecciones incoherentes.
        if ("VARIAS_OPCIONES".equalsIgnoreCase(encuesta.getTipoEncuesta())) {
            for (OpcionVoto opcion : opcionesVoto) {
                CheckBox checkBox = new CheckBox(opcion.getTextoOpcion());
                checkBox.setUserData(opcion);
                checkBox.getStyleClass().add("option-choice");
                opciones.getChildren().add(checkBox);
            }
        } else {
            ToggleGroup grupo = new ToggleGroup();
            for (OpcionVoto opcion : opcionesVoto) {
                RadioButton radioButton = new RadioButton(opcion.getTextoOpcion());
                radioButton.setUserData(opcion);
                radioButton.setToggleGroup(grupo);
                radioButton.getStyleClass().add("option-choice");
                opciones.getChildren().add(radioButton);
            }
        }

        Button votar = new Button("Registrar voto");
        votar.getStyleClass().add("module-button");
        votar.setOnAction(event -> registrarVoto(encuesta, opciones));

        detalle.getChildren().addAll(descripcion, tituloOpciones, opciones, votar);
        contenidoUsuario.getChildren().setAll(cabecera, detalle);
    }

    private void registrarVoto(Encuesta encuesta, VBox opciones) {
        if (usuarioActual == null) {
            mostrarAlerta("Error", "No se pudo identificar el usuario actual.");
            return;
        }

        // Cada control guarda su OpcionVoto en userData para recuperar el ID real de base de datos.
        List<Integer> idsOpciones = opciones.getChildren().stream()
                .filter(node -> node instanceof RadioButton || node instanceof CheckBox)
                .filter(node -> {
                    if (node instanceof RadioButton radioButton) {
                        return radioButton.isSelected();
                    }
                    return ((CheckBox) node).isSelected();
                })
                .map(node -> {
                    Object data = node.getUserData();
                    return data instanceof OpcionVoto opcion ? opcion.getIdOpcion() : 0;
                })
                .filter(idOpcion -> idOpcion > 0)
                .toList();

        if (idsOpciones.isEmpty()) {
            mostrarAlerta("Seleccion obligatoria", "Debes seleccionar al menos una opcion para votar.");
            return;
        }

        if ("UNA_OPCION".equalsIgnoreCase(encuesta.getTipoEncuesta()) && idsOpciones.size() > 1) {
            mostrarAlerta("Seleccion incorrecta", "Esta encuesta solo permite elegir una opcion.");
            return;
        }

        if (encuestaDAO.registrarVoto(encuesta.getIdEncuesta(), usuarioActual.getIdUsuario(), idsOpciones)) {
            mostrarAlerta("Voto registrado", "Tu voto se ha registrado correctamente.");
            mostrarVotacionesDisponibles();
        } else {
            String detalle = encuestaDAO.getUltimoError();
            mostrarAlerta("Error", "No se pudo registrar el voto.\n\nDetalle: "
                    + (detalle == null || detalle.isBlank() ? "No hay detalle disponible." : detalle));
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMiParticipacion() {
        HBox cabecera = crearCabeceraInterna(
                "Mi participacion",
                "Historial de votaciones en las que ya has participado.");

        VBox listado = new VBox(14);
        listado.getStyleClass().add("table-card");
        listado.setPadding(new Insets(22));

        List<ParticipacionUsuario> participaciones = usuarioActual == null
                ? List.of()
                : encuestaDAO.obtenerParticipacionUsuario(usuarioActual.getIdUsuario());

        Label tituloListado = new Label("Votaciones realizadas");
        tituloListado.getStyleClass().add("section-title");
        listado.getChildren().add(tituloListado);

        if (participaciones.isEmpty()) {
            Label sinParticipacion = new Label("Todavia no has participado en ninguna votacion.");
            sinParticipacion.getStyleClass().add("module-text");
            listado.getChildren().add(sinParticipacion);
        } else {
            for (ParticipacionUsuario participacion : participaciones) {
                listado.getChildren().add(crearTarjetaParticipacion(participacion));
            }
        }

        ScrollPane scrollPane = new ScrollPane(listado);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        contenidoUsuario.getChildren().setAll(cabecera, scrollPane);
    }

    private VBox crearTarjetaParticipacion(ParticipacionUsuario participacion) {
        VBox tarjeta = new VBox(10);
        tarjeta.getStyleClass().add("module-card");
        tarjeta.setPadding(new Insets(18));

        Label titulo = new Label(participacion.getTituloEncuesta());
        titulo.getStyleClass().add("module-title");
        titulo.setWrapText(true);

        Label opciones = new Label("Seleccion: " + participacion.getOpcionesElegidas());
        opciones.getStyleClass().add("module-text");
        opciones.setWrapText(true);

        Label datos = new Label("Fecha: " + participacion.getFechaVoto().toLocalDate()
                + "   |   Tipo: " + participacion.getTipoEncuesta()
                + "   |   Codigo: " + participacion.getCodigoVerificacion());
        datos.getStyleClass().add("info-text");
        datos.setWrapText(true);

        tarjeta.getChildren().addAll(titulo, opciones, datos);
        return tarjeta;
    }
}
