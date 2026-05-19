package com.proyecto.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.proyecto.ConexionBD;
import com.proyecto.modelos.Subcategoria;

public class SubcategoriaDAO {

    // Inserta una subcategoria y guarda en el modelo el ID generado por MySQL.
    public boolean insertarSubcategoria(Subcategoria subcategoria) {
        String sql = "INSERT INTO subcategorias (nombre) VALUES (?)";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, subcategoria.getNombre());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    subcategoria.setIdSubcategoria(rs.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar subcategoria: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarSubcategoria(Subcategoria subcategoria) {
        String sql = "UPDATE subcategorias SET nombre = ? WHERE id_subcategoria = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, subcategoria.getNombre());
            ps.setInt(2, subcategoria.getIdSubcategoria());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al actualizar subcategoria: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarSubcategoria(int idSubcategoria) {
        String sql = "DELETE FROM subcategorias WHERE id_subcategoria = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSubcategoria);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al eliminar subcategoria: " + e.getMessage());
            return false;
        }
    }

    public List<Subcategoria> obtenerSubcategorias() {
        List<Subcategoria> lista = new ArrayList<>();
        // LEFT JOIN mantiene visibles las subcategorias aunque todavia no tengan usuarios.
        String sql = """
                SELECT s.id_subcategoria, s.nombre, COUNT(us.id_usuario) AS usuarios_asociados
                FROM subcategorias s
                LEFT JOIN usuario_subcategoria us ON us.id_subcategoria = s.id_subcategoria
                GROUP BY s.id_subcategoria, s.nombre
                ORDER BY s.nombre ASC
                """;

        try (Connection conn = ConexionBD.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearSubcategoria(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener subcategorias: " + e.getMessage());
        }

        return lista;
    }

    public List<Subcategoria> buscarSubcategorias(String busqueda) {
        List<Subcategoria> lista = new ArrayList<>();
        String sql = """
                SELECT s.id_subcategoria, s.nombre, COUNT(us.id_usuario) AS usuarios_asociados
                FROM subcategorias s
                LEFT JOIN usuario_subcategoria us ON us.id_subcategoria = s.id_subcategoria
                WHERE s.nombre LIKE ?
                GROUP BY s.id_subcategoria, s.nombre
                ORDER BY s.nombre ASC
                LIMIT 50
                """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + busqueda + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearSubcategoria(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar subcategorias: " + e.getMessage());
        }

        return lista;
    }

    public int contarUsuariosAsociados(int idSubcategoria) {
        String sql = "SELECT COUNT(*) AS total FROM usuario_subcategoria WHERE id_subcategoria = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSubcategoria);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("Error al contar usuarios asociados: " + e.getMessage());
        }

        return 0;
    }

    private Subcategoria mapearSubcategoria(ResultSet rs) throws SQLException {
        // Convierte una fila SQL en un objeto del modelo para usarlo en la interfaz.
        return new Subcategoria(
                rs.getInt("id_subcategoria"),
                rs.getString("nombre"),
                rs.getInt("usuarios_asociados")
        );
    }
}
