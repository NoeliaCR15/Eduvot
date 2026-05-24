package com.proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.proyecto.ConexionBD;
import com.proyecto.modelos.Encuesta;
import com.proyecto.modelos.OpcionVoto;
import com.proyecto.modelos.ParticipacionUsuario;
import com.proyecto.modelos.ResultadoOpcion;

public class EncuestaDAO {

    private String ultimoError = "";

    public EncuestaDAO() {
        asegurarTablaEncuestaSubcategoria();
        asegurarTablaEncuestasArchivadas();
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public boolean insertarEncuesta(Encuesta encuesta, List<String> opciones, List<String> subcategorias) {
        ultimoError = "";
        String sql = """
                INSERT INTO encuestas (titulo, descripcion, fecha_inicio, fecha_fin, activa, tipo_encuesta, creada_por)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Encuesta, opciones y destinatarios se guardan como una unidad para evitar datos a medias.
            conn.setAutoCommit(false);

            ps.setString(1, encuesta.getTitulo());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(encuesta.getFechaInicio()));
            ps.setTimestamp(4, Timestamp.valueOf(encuesta.getFechaFin()));
            ps.setBoolean(5, encuesta.isActiva());
            ps.setString(6, encuesta.getTipoEncuesta());
            ps.setInt(7, encuesta.getCreadaPor());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    encuesta.setIdEncuesta(rs.getInt(1));
                }
            }

            guardarOpcionesEncuesta(conn, encuesta.getIdEncuesta(), opciones);
            guardarSubcategoriasEncuesta(conn, encuesta.getIdEncuesta(), subcategorias);
            conn.commit();
            return true;

        } catch (SQLException e) {
            registrarError("Error al insertar encuesta", e);
            return false;
        }
    }

    public boolean actualizarEncuesta(Encuesta encuesta, List<String> opciones, List<String> subcategorias) {
        ultimoError = "";
        String sql = """
                UPDATE encuestas
                SET titulo = ?, descripcion = ?, fecha_inicio = ?, fecha_fin = ?, activa = ?, tipo_encuesta = ?
                WHERE id_encuesta = ?
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            ps.setString(1, encuesta.getTitulo());
            ps.setString(2, encuesta.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(encuesta.getFechaInicio()));
            ps.setTimestamp(4, Timestamp.valueOf(encuesta.getFechaFin()));
            ps.setBoolean(5, encuesta.isActiva());
            ps.setString(6, encuesta.getTipoEncuesta());
            ps.setInt(7, encuesta.getIdEncuesta());
            ps.executeUpdate();

            guardarOpcionesEncuesta(conn, encuesta.getIdEncuesta(), opciones);
            guardarSubcategoriasEncuesta(conn, encuesta.getIdEncuesta(), subcategorias);
            conn.commit();
            return true;

        } catch (SQLException e) {
            registrarError("Error al actualizar encuesta", e);
            return false;
        }
    }

    public boolean eliminarEncuesta(int idEncuesta) {
        ultimoError = "";
        String borrarSubcategoriasSql = "DELETE FROM encuesta_subcategoria WHERE id_encuesta = ?";
        String borrarOpcionesSql = "DELETE FROM opciones_voto WHERE id_encuesta = ?";
        String borrarEncuestaSql = "DELETE FROM encuestas WHERE id_encuesta = ?";

        try (Connection conn = ConexionBD.getConexion()) {
            try (PreparedStatement psSubcategorias = conn.prepareStatement(borrarSubcategoriasSql)) {
                psSubcategorias.setInt(1, idEncuesta);
                psSubcategorias.executeUpdate();
            }

            try (PreparedStatement psOpciones = conn.prepareStatement(borrarOpcionesSql)) {
                psOpciones.setInt(1, idEncuesta);
                psOpciones.executeUpdate();
            }

            try (PreparedStatement psEncuesta = conn.prepareStatement(borrarEncuestaSql)) {
                psEncuesta.setInt(1, idEncuesta);
                psEncuesta.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            registrarError("Error al eliminar encuesta", e);
            return false;
        }
    }

    public List<Encuesta> obtenerEncuestas() {
        List<Encuesta> encuestas = new ArrayList<>();
        String sql = """
                SELECT e.*,
                       COUNT(DISTINCT o.id_opcion) AS total_opciones,
                       COUNT(DISTINCT v.id_voto) AS total_votos
                FROM encuestas e
                LEFT JOIN opciones_voto o ON o.id_encuesta = e.id_encuesta
                LEFT JOIN votos v ON v.id_encuesta = e.id_encuesta
                LEFT JOIN encuestas_archivadas ea ON ea.id_encuesta = e.id_encuesta
                WHERE ea.id_encuesta IS NULL
                GROUP BY e.id_encuesta
                ORDER BY e.fecha_inicio DESC, e.titulo ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                encuestas.add(mapearEncuesta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener encuestas: " + e.getMessage());
        }

        return encuestas;
    }

    public List<Encuesta> buscarEncuestas(String busqueda) {
        List<Encuesta> encuestas = new ArrayList<>();
        String sql = """
                SELECT e.*,
                       COUNT(DISTINCT o.id_opcion) AS total_opciones,
                       COUNT(DISTINCT v.id_voto) AS total_votos
                FROM encuestas e
                LEFT JOIN opciones_voto o ON o.id_encuesta = e.id_encuesta
                LEFT JOIN votos v ON v.id_encuesta = e.id_encuesta
                LEFT JOIN encuestas_archivadas ea ON ea.id_encuesta = e.id_encuesta
                WHERE ea.id_encuesta IS NULL
                  AND (e.titulo LIKE ? OR e.descripcion LIKE ?)
                GROUP BY e.id_encuesta
                ORDER BY e.fecha_inicio DESC, e.titulo ASC
                LIMIT 50
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String patron = "%" + busqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                encuestas.add(mapearEncuesta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar encuestas: " + e.getMessage());
        }

        return encuestas;
    }

    public List<Encuesta> obtenerEncuestasDisponiblesUsuario(int idUsuario) {
        List<Encuesta> encuestas = new ArrayList<>();
        String sql = """
                SELECT e.*,
                       COUNT(DISTINCT o.id_opcion) AS total_opciones,
                       COUNT(DISTINCT v.id_voto) AS total_votos
                FROM encuestas e
                INNER JOIN encuesta_subcategoria es ON es.id_encuesta = e.id_encuesta
                INNER JOIN usuario_subcategoria us ON us.id_subcategoria = es.id_subcategoria
                LEFT JOIN opciones_voto o ON o.id_encuesta = e.id_encuesta
                LEFT JOIN votos v ON v.id_encuesta = e.id_encuesta
                LEFT JOIN encuestas_archivadas ea ON ea.id_encuesta = e.id_encuesta
                WHERE us.id_usuario = ?
                  AND ea.id_encuesta IS NULL
                  AND e.activa = true
                  AND NOW() BETWEEN e.fecha_inicio AND e.fecha_fin
                  AND NOT EXISTS (
                      SELECT 1
                      FROM votos voto_usuario
                      WHERE voto_usuario.id_encuesta = e.id_encuesta
                        AND voto_usuario.id_usuario = ?
                  )
                GROUP BY e.id_encuesta
                ORDER BY e.fecha_fin ASC, e.titulo ASC
                """;

        // Solo devuelve encuestas activas, dentro de fecha, no archivadas y aun no votadas.
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                encuestas.add(mapearEncuesta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener encuestas disponibles: " + e.getMessage());
        }

        return encuestas;
    }

    public List<Encuesta> obtenerEncuestasArchivadas() {
        List<Encuesta> encuestas = new ArrayList<>();
        String sql = """
                SELECT e.*,
                       COUNT(DISTINCT o.id_opcion) AS total_opciones,
                       COUNT(DISTINCT v.id_voto) AS total_votos
                FROM encuestas e
                INNER JOIN encuestas_archivadas ea ON ea.id_encuesta = e.id_encuesta
                LEFT JOIN opciones_voto o ON o.id_encuesta = e.id_encuesta
                LEFT JOIN votos v ON v.id_encuesta = e.id_encuesta
                GROUP BY e.id_encuesta
                ORDER BY ea.fecha_archivo DESC, e.titulo ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                encuestas.add(mapearEncuesta(rs));
            }

        } catch (SQLException e) {
            registrarError("Error al obtener encuestas archivadas", e);
        }

        return encuestas;
    }

    public boolean encuestaArchivada(int idEncuesta) {
        String sql = "SELECT COUNT(*) AS total FROM encuestas_archivadas WHERE id_encuesta = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("total") > 0;

        } catch (SQLException e) {
            registrarError("Error al comprobar encuesta archivada", e);
            return false;
        }
    }

    public boolean archivarEncuesta(int idEncuesta, int idUsuario, String motivo) {
        ultimoError = "";
        String sql = """
                INSERT INTO encuestas_archivadas (id_encuesta, motivo, archivada_por)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ps.setString(2, motivo == null || motivo.isBlank() ? "Encuesta finalizada" : motivo.trim());
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            registrarError("Error al archivar encuesta", e);
            return false;
        }
    }

    public List<String> obtenerOpcionesEncuesta(int idEncuesta) {
        List<String> opciones = new ArrayList<>();
        String sql = "SELECT texto_opcion FROM opciones_voto WHERE id_encuesta = ? ORDER BY id_opcion ASC";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                opciones.add(rs.getString("texto_opcion"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener opciones de encuesta: " + e.getMessage());
        }

        return opciones;
    }

    public List<OpcionVoto> obtenerOpcionesVotoEncuesta(int idEncuesta) {
        List<OpcionVoto> opciones = new ArrayList<>();
        String sql = "SELECT id_opcion, id_encuesta, texto_opcion FROM opciones_voto WHERE id_encuesta = ? ORDER BY id_opcion ASC";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                opciones.add(new OpcionVoto(
                        rs.getInt("id_opcion"),
                        rs.getInt("id_encuesta"),
                        rs.getString("texto_opcion")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener opciones detalladas de encuesta: " + e.getMessage());
        }

        return opciones;
    }

    public boolean usuarioYaVoto(int idEncuesta, int idUsuario) {
        String sql = "SELECT COUNT(*) AS total FROM votos WHERE id_encuesta = ? AND id_usuario = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al comprobar voto de usuario: " + e.getMessage());
        }

        return false;
    }

    public boolean registrarVoto(int idEncuesta, int idUsuario, List<Integer> idsOpciones) {
        ultimoError = "";
        String votoSql = """
                INSERT INTO votos (id_encuesta, id_usuario, codigo_verificacion, fecha_voto)
                VALUES (?, ?, ?, NOW())
                """;
        String detalleSql = "INSERT INTO detalle_voto (id_voto, id_opcion) VALUES (?, ?)";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement psVoto = conn.prepareStatement(votoSql, Statement.RETURN_GENERATED_KEYS)) {

            // El voto cabecera y sus opciones se insertan juntos; si falla una parte, se cancela todo.
            conn.setAutoCommit(false);

            if (usuarioYaVoto(conn, idEncuesta, idUsuario)) {
                ultimoError = "El usuario ya ha votado en esta encuesta.";
                conn.rollback();
                return false;
            }

            psVoto.setInt(1, idEncuesta);
            psVoto.setInt(2, idUsuario);
            psVoto.setString(3, UUID.randomUUID().toString());
            psVoto.executeUpdate();

            int idVoto = 0;
            try (ResultSet rs = psVoto.getGeneratedKeys()) {
                if (rs.next()) {
                    idVoto = rs.getInt(1);
                }
            }

            try (PreparedStatement psDetalle = conn.prepareStatement(detalleSql)) {
                for (Integer idOpcion : idsOpciones) {
                    psDetalle.setInt(1, idVoto);
                    psDetalle.setInt(2, idOpcion);
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            registrarError("Error al registrar voto", e);
            return false;
        }
    }

    private boolean usuarioYaVoto(Connection conn, int idEncuesta, int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM votos WHERE id_encuesta = ? AND id_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEncuesta);
            ps.setInt(2, idUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("total") > 0;
        }
    }

    public List<String> obtenerSubcategoriasEncuesta(int idEncuesta) {
        List<String> subcategorias = new ArrayList<>();
        String sql = """
                SELECT s.nombre
                FROM encuesta_subcategoria es
                INNER JOIN subcategorias s ON s.id_subcategoria = es.id_subcategoria
                WHERE es.id_encuesta = ?
                ORDER BY s.nombre ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                subcategorias.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener subcategorias de encuesta: " + e.getMessage());
        }

        return subcategorias;
    }

    public int contarVotosEncuesta(int idEncuesta) {
        String sql = "SELECT COUNT(*) AS total FROM votos WHERE id_encuesta = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Error al contar votos de encuesta: " + e.getMessage());
        }

        return 0;
    }

    public List<ParticipacionUsuario> obtenerParticipacionUsuario(int idUsuario) {
        List<ParticipacionUsuario> participaciones = new ArrayList<>();
        String sql = """
                SELECT v.id_voto,
                       e.titulo,
                       e.tipo_encuesta,
                       v.fecha_voto,
                       v.codigo_verificacion,
                       GROUP_CONCAT(o.texto_opcion ORDER BY o.texto_opcion SEPARATOR ', ') AS opciones_elegidas
                FROM votos v
                INNER JOIN encuestas e ON e.id_encuesta = v.id_encuesta
                INNER JOIN detalle_voto dv ON dv.id_voto = v.id_voto
                INNER JOIN opciones_voto o ON o.id_opcion = dv.id_opcion
                WHERE v.id_usuario = ?
                GROUP BY v.id_voto, e.titulo, e.tipo_encuesta, v.fecha_voto, v.codigo_verificacion
                ORDER BY v.fecha_voto DESC
                """;

        // GROUP_CONCAT resume las opciones elegidas para mostrarlas en una sola tarjeta de actividad.
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                participaciones.add(new ParticipacionUsuario(
                        rs.getInt("id_voto"),
                        rs.getString("titulo"),
                        rs.getString("tipo_encuesta"),
                        rs.getTimestamp("fecha_voto").toLocalDateTime(),
                        rs.getString("opciones_elegidas"),
                        rs.getString("codigo_verificacion")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener participacion del usuario: " + e.getMessage());
        }

        return participaciones;
    }

    public List<ResultadoOpcion> obtenerResultadosEncuesta(int idEncuesta) {
        List<ResultadoOpcion> resultados = new ArrayList<>();
        String sql = """
                SELECT o.id_opcion,
                       o.texto_opcion,
                       COUNT(dv.id_voto) AS total_votos
                FROM opciones_voto o
                LEFT JOIN detalle_voto dv ON dv.id_opcion = o.id_opcion
                WHERE o.id_encuesta = ?
                GROUP BY o.id_opcion, o.texto_opcion
                ORDER BY total_votos DESC, o.texto_opcion ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultados.add(new ResultadoOpcion(
                        rs.getInt("id_opcion"),
                        rs.getString("texto_opcion"),
                        rs.getInt("total_votos")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener resultados de encuesta: " + e.getMessage());
        }

        return resultados;
    }

    public int contarVotantesDestinatariosEncuesta(int idEncuesta) {
        String sql = """
                SELECT COUNT(DISTINCT us.id_usuario) AS total
                FROM encuesta_subcategoria es
                INNER JOIN usuario_subcategoria us ON us.id_subcategoria = es.id_subcategoria
                INNER JOIN usuarios u ON u.id_usuario = us.id_usuario
                WHERE es.id_encuesta = ?
                  AND u.activo = true
                  AND u.es_administrador = false
                """;

        // Cuenta usuarios unicos porque una persona puede pertenecer a varias subcategorias.
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEncuesta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Error al contar destinatarios de encuesta: " + e.getMessage());
        }

        return 0;
    }

    private void asegurarTablaEncuestaSubcategoria() {
        String sql = """
                CREATE TABLE IF NOT EXISTS encuesta_subcategoria (
                    id_encuesta INT NOT NULL,
                    id_subcategoria INT NOT NULL,
                    PRIMARY KEY (id_encuesta, id_subcategoria),
                    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta),
                    FOREIGN KEY (id_subcategoria) REFERENCES subcategorias(id_subcategoria)
                )
                """;

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement()) {

            st.executeUpdate(sql);

        } catch (SQLException e) {
            registrarError("Error al asegurar tabla encuesta_subcategoria", e);
        }
    }

    private void asegurarTablaEncuestasArchivadas() {
        String sql = """
                CREATE TABLE IF NOT EXISTS encuestas_archivadas (
                    id_archivo INT AUTO_INCREMENT PRIMARY KEY,
                    id_encuesta INT NOT NULL UNIQUE,
                    fecha_archivo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    motivo VARCHAR(150),
                    archivada_por INT,
                    FOREIGN KEY (id_encuesta) REFERENCES encuestas(id_encuesta),
                    FOREIGN KEY (archivada_por) REFERENCES usuarios(id_usuario)
                )
                """;

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement()) {

            st.executeUpdate(sql);

        } catch (SQLException e) {
            registrarError("Error al asegurar tabla encuestas_archivadas", e);
        }
    }

    private void guardarOpcionesEncuesta(Connection conn, int idEncuesta, List<String> opciones) throws SQLException {
        String borrarSql = "DELETE FROM opciones_voto WHERE id_encuesta = ?";
        String insertarSql = "INSERT INTO opciones_voto (id_encuesta, texto_opcion) VALUES (?, ?)";

        // En edicion se reemplaza la lista completa de opciones por la seleccion actual del formulario.
        try (PreparedStatement psBorrar = conn.prepareStatement(borrarSql)) {
            psBorrar.setInt(1, idEncuesta);
            psBorrar.executeUpdate();
        }

        try (PreparedStatement psInsertar = conn.prepareStatement(insertarSql)) {
            for (String opcion : opciones) {
                psInsertar.setInt(1, idEncuesta);
                psInsertar.setString(2, opcion);
                psInsertar.addBatch();
            }
            psInsertar.executeBatch();
        }
    }

    private void guardarSubcategoriasEncuesta(Connection conn, int idEncuesta, List<String> subcategorias) throws SQLException {
        String borrarSql = "DELETE FROM encuesta_subcategoria WHERE id_encuesta = ?";
        String insertarSql = """
                INSERT INTO encuesta_subcategoria (id_encuesta, id_subcategoria)
                SELECT ?, id_subcategoria
                FROM subcategorias
                WHERE nombre = ?
                """;

        // La relacion N:M permite que una votacion vaya dirigida a varios colectivos.
        try (PreparedStatement psBorrar = conn.prepareStatement(borrarSql)) {
            psBorrar.setInt(1, idEncuesta);
            psBorrar.executeUpdate();
        }

        try (PreparedStatement psInsertar = conn.prepareStatement(insertarSql)) {
            for (String subcategoria : subcategorias) {
                psInsertar.setInt(1, idEncuesta);
                psInsertar.setString(2, subcategoria);
                psInsertar.addBatch();
            }
            psInsertar.executeBatch();
        }
    }

    private Encuesta mapearEncuesta(ResultSet rs) throws SQLException {
        return new Encuesta(
                rs.getInt("id_encuesta"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                rs.getTimestamp("fecha_inicio").toLocalDateTime(),
                rs.getTimestamp("fecha_fin").toLocalDateTime(),
                rs.getBoolean("activa"),
                rs.getString("tipo_encuesta"),
                rs.getInt("creada_por"),
                rs.getInt("total_opciones"),
                rs.getInt("total_votos")
        );
    }

    private void registrarError(String contexto, SQLException e) {
        ultimoError = e.getMessage();
        System.out.println(contexto + ": " + ultimoError);
    }
}
