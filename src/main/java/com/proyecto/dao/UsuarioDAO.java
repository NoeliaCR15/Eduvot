package com.proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.ConexionBD;
import com.proyecto.modelos.Usuario;

public class UsuarioDAO {

    // Inserta el usuario principal y guarda en el modelo el ID generado.
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (dni, nombre_usuario, password, es_administrador, activo, grupo_usuario) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getPassword());
            ps.setBoolean(4, usuario.isEsAdministrador());
            ps.setBoolean(5, usuario.isActivo());
            ps.setString(6, usuario.getGrupo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> obtenerUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_usuario ASC";

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("dni"),
                        rs.getString("nombre_usuario"),
                        rs.getString("password"),
                        rs.getBoolean("es_administrador"),
                        rs.getBoolean("activo"),
                        rs.getString("grupo_usuario")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    public List<Usuario> buscarUsuarios(String busqueda) {
        List<Usuario> lista = new ArrayList<>();
        String sql = """
                SELECT *
                FROM usuarios
                WHERE dni LIKE ? OR nombre_usuario LIKE ?
                ORDER BY nombre_usuario ASC
                LIMIT 50
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String patron = "%" + busqueda + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("dni"),
                        rs.getString("nombre_usuario"),
                        rs.getString("password"),
                        rs.getBoolean("es_administrador"),
                        rs.getBoolean("activo"),
                        rs.getString("grupo_usuario")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET dni = ?, nombre_usuario = ?, password = ?, es_administrador = ?, activo = ?, grupo_usuario = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getNombreUsuario());
            ps.setString(3, usuario.getPassword());
            ps.setBoolean(4, usuario.isEsAdministrador());
            ps.setBoolean(5, usuario.isActivo());
            ps.setString(6, usuario.getGrupo());
            ps.setInt(7, usuario.getIdUsuario());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUsuario(int idUsuario) {
        // Primero se eliminan relaciones dependientes para respetar las claves foraneas.
        String borrarFamiliaComoFamiliarSql = "DELETE FROM usuario_familia WHERE id_familiar = ?";
        String borrarFamiliaComoAlumnoSql = "DELETE FROM usuario_familia WHERE id_alumno = ?";
        String borrarSubcategoriasSql = "DELETE FROM usuario_subcategoria WHERE id_usuario = ?";
        String borrarUsuarioSql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = ConexionBD.getConexion()) {
            try (PreparedStatement psFamilia = conn.prepareStatement(borrarFamiliaComoFamiliarSql)) {
                psFamilia.setInt(1, idUsuario);
                psFamilia.executeUpdate();
            }

            try (PreparedStatement psAlumno = conn.prepareStatement(borrarFamiliaComoAlumnoSql)) {
                psAlumno.setInt(1, idUsuario);
                psAlumno.executeUpdate();
            }

            try (PreparedStatement psSubcategorias = conn.prepareStatement(borrarSubcategoriasSql)) {
                psSubcategorias.setInt(1, idUsuario);
                psSubcategorias.executeUpdate();
            }

            try (PreparedStatement psUsuario = conn.prepareStatement(borrarUsuarioSql)) {
                psUsuario.setInt(1, idUsuario);
                psUsuario.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("dni"),
                        rs.getString("nombre_usuario"),
                        rs.getString("password"),
                        rs.getBoolean("es_administrador"),
                        rs.getBoolean("activo"),
                        rs.getString("grupo_usuario")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    public List<String> obtenerSubcategorias() {
        List<String> subcategorias = new ArrayList<>();
        String sql = "SELECT nombre FROM subcategorias ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                subcategorias.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener subcategorias: " + e.getMessage());
        }

        return subcategorias;
    }

    public List<String> obtenerSubcategoriasUsuario(int idUsuario) {
        List<String> subcategorias = new ArrayList<>();
        String sql = """
                SELECT s.nombre
                FROM usuario_subcategoria us
                INNER JOIN subcategorias s ON s.id_subcategoria = us.id_subcategoria
                WHERE us.id_usuario = ?
                ORDER BY s.nombre ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                subcategorias.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener subcategorias del usuario: " + e.getMessage());
        }

        return subcategorias;
    }

    public List<Usuario> obtenerAlumnos() {
        List<Usuario> alumnos = new ArrayList<>();
        // Solo se muestran usuarios marcados como Alumnado en usuario_subcategoria.
        String sql = """
                SELECT DISTINCT u.*
                FROM usuarios u
                INNER JOIN usuario_subcategoria us ON us.id_usuario = u.id_usuario
                INNER JOIN subcategorias s ON s.id_subcategoria = us.id_subcategoria
                WHERE LOWER(s.nombre) = 'alumnado'
                ORDER BY u.nombre_usuario ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                alumnos.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    public List<Usuario> obtenerAlumnosFamilia(int idFamiliar) {
        List<Usuario> alumnos = new ArrayList<>();
        String sql = """
                SELECT u.*
                FROM usuario_familia uf
                INNER JOIN usuarios u ON u.id_usuario = uf.id_alumno
                WHERE uf.id_familiar = ?
                ORDER BY u.nombre_usuario ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idFamiliar);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                alumnos.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener alumnos de familia: " + e.getMessage());
        }

        return alumnos;
    }

    public boolean guardarAlumnosFamilia(int idFamiliar, List<Integer> idsAlumnos) {
        // Se reemplazan las relaciones antiguas por la seleccion actual del formulario.
        String borrarSql = "DELETE FROM usuario_familia WHERE id_familiar = ?";
        String insertarSql = "INSERT INTO usuario_familia (id_familiar, id_alumno) VALUES (?, ?)";

        try (Connection conn = ConexionBD.getConexion()) {
            try (PreparedStatement psBorrar = conn.prepareStatement(borrarSql)) {
                psBorrar.setInt(1, idFamiliar);
                psBorrar.executeUpdate();
            }

            try (PreparedStatement psInsertar = conn.prepareStatement(insertarSql)) {
                for (Integer idAlumno : idsAlumnos) {
                    psInsertar.setInt(1, idFamiliar);
                    psInsertar.setInt(2, idAlumno);
                    psInsertar.addBatch();
                }
                psInsertar.executeBatch();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al guardar alumnos de familia: " + e.getMessage());
            return false;
        }
    }

    public boolean guardarSubcategoriasUsuario(int idUsuario, List<String> subcategorias) {
        // Igual que con familia: se borra la seleccion previa y se guarda la nueva.
        String borrarSql = "DELETE FROM usuario_subcategoria WHERE id_usuario = ?";
        String insertarSql = """
                INSERT INTO usuario_subcategoria (id_usuario, id_subcategoria)
                SELECT ?, id_subcategoria
                FROM subcategorias
                WHERE nombre = ?
                """;

        try (Connection conn = ConexionBD.getConexion()) {
            try (PreparedStatement psBorrar = conn.prepareStatement(borrarSql)) {
                psBorrar.setInt(1, idUsuario);
                psBorrar.executeUpdate();
            }

            try (PreparedStatement psInsertar = conn.prepareStatement(insertarSql)) {
                for (String subcategoria : subcategorias) {
                    psInsertar.setInt(1, idUsuario);
                    psInsertar.setString(2, subcategoria);
                    psInsertar.addBatch();
                }
                psInsertar.executeBatch();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al guardar subcategorias del usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario validarLogin(String dni, String password) {
        String sql = "SELECT * FROM usuarios WHERE dni = ? AND password = ? AND activo = true";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("dni"),
                        rs.getString("nombre_usuario"),
                        rs.getString("password"),
                        rs.getBoolean("es_administrador"),
                        rs.getBoolean("activo"),
                        rs.getString("grupo_usuario")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al validar login: " + e.getMessage());
        }
        return null;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        // Centraliza la conversion de una fila SQL al modelo Usuario.
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("dni"),
                rs.getString("nombre_usuario"),
                rs.getString("password"),
                rs.getBoolean("es_administrador"),
                rs.getBoolean("activo"),
                rs.getString("grupo_usuario")
        );
    }
}
